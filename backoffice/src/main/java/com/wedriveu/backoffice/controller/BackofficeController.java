package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.model.BackOfficeModel;
import com.wedriveu.backoffice.util.EventBus;
import com.wedriveu.backoffice.view.BackOfficeView;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Stefano Bernagozzi
 */
public class BackofficeController extends AbstractVerticle {
    private Future futureModel;
    private BackOfficeModel backOfficeModel;
    private BackOfficeView backOfficeView;
    Vertx vertx;
    String toUndeploy;

    public BackofficeController(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        init();
        vertx.eventBus().consumer(EventBus.BACKOFFICE_CONTROLLER, this::updateCounter);
    }


    private void init() {
        List<Future> futures = new ArrayList<>();
        backOfficeModel = new BackOfficeModel();
        backOfficeView = new BackOfficeView();
        backOfficeView.setVisible(true);
        ButtonEventListener buttonEventListener = new ButtonEventListener(backOfficeModel, backOfficeView);
        backOfficeView.addButtonListener(buttonEventListener);
        futureModel = backOfficeModel.getFuture();
        futureModel.setHandler(res -> {
            vertx.deployVerticle(new BackofficeVehiclesResponseConsumer(
                    "." + backOfficeModel.getBackofficeID() + ".updates",
                    ""));
            vertx.deployVerticle(new BackofficeVehiclesResponseConsumer(
                    "." + backOfficeModel.getBackofficeID(),
                    "." + backOfficeModel.getBackofficeID()));
            vertx.deployVerticle(new BackofficeVehicleRequestPublisher(
                    backOfficeModel.getBackofficeID()),
                    resDeploy ->{ });
            vertx.deployVerticle(new BackofficeBookingsRequestPublisher(
                            backOfficeModel.getBackofficeID()),
                            resDeploy ->{ });
            vertx.deployVerticle(new BackofficeBookingsResponseConsumer(
                            backOfficeModel.getBackofficeID()),
                            resDeploy ->{ });
        });
    }

    private void updateCounter(Message message) {
        VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleCounter.class);
        backOfficeModel.updateCounter(vehicleCounter);
        backOfficeView.updateText(vehicleCounter);
    }
}
