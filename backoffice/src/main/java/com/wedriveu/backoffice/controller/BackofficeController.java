package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.model.BackOfficeModel;
import com.wedriveu.backoffice.util.EventBus;
import com.wedriveu.backoffice.view.BackOfficeView;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stefano Bernagozzi
 */
public class BackofficeController extends AbstractVerticle {
    private Future futureModel;
    private BackOfficeModel backOfficeModel;
    private BackOfficeView backOfficeView;
    Vertx vertx;

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
            Future listener1 = Future.future();
            vertx.deployVerticle(new AnalyticsVehiclesResponseConsumer("." + backOfficeModel.getBackofficeID() + ".updates", ""), listener1.completer());
            Future listener2 = Future.future();
            vertx.deployVerticle(new AnalyticsVehiclesResponseConsumer("." + backOfficeModel.getBackofficeID(), "." + backOfficeModel.getBackofficeID()), listener2.completer());
            Future listener3 = Future.future();
            vertx.deployVerticle(new RabbitmqInitialRequest(backOfficeModel.getBackofficeID()), listener3);
            futures.add(listener1);
            futures.add(listener2);
        });
    }

    private void updateCounter(Message message) {
        VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleCounter.class);
        backOfficeModel.updateCounter(vehicleCounter);
        backOfficeView.updateText(vehicleCounter);
    }
}
