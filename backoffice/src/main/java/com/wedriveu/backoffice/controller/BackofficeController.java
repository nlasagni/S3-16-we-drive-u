package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.model.BackOfficeModel;
import com.wedriveu.backoffice.view.GraphicViewer;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.wedriveu.shared.util.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class BackofficeController extends AbstractVerticle{
    private Future futureModel;
    private BackOfficeModel backOfficeModel;
    private GraphicViewer graphicViewer;
    Vertx vertx;

    public BackofficeController( Vertx vertx){
        this.vertx = vertx;
    }

    @Override
    public void start(Future futureRetriever) throws Exception{
        init();
        vertx.eventBus().consumer(BACKOFFICE_CONTROLLER_EVENTBUS, this::updateCounter);
    }


    private void init() {
        List<Future> futures = new ArrayList<>();
        backOfficeModel = new BackOfficeModel();
        graphicViewer = new GraphicViewer();
        graphicViewer.setVisible(true);
        ButtonEventListener buttonEventListener = new ButtonEventListener(backOfficeModel, graphicViewer);
        graphicViewer.addButtonListener(buttonEventListener);
        futureModel = backOfficeModel.getFuture();
        futureModel.setHandler(res-> {
            Future listener1 = Future.future();
            vertx.deployVerticle(new RabbitmqListenerUpdate(backOfficeModel.getBackofficeID()), listener1.completer());
            Future listener2 = Future.future();
            vertx.deployVerticle(new RabbitmqListenerUpdate(""),listener2.completer() );
            futures.add(listener1);
            futures.add(listener2);

            CompositeFuture.all(futures).setHandler(futureRes->{
               vertx.deployVerticle(new RabbitmqInitialRequest(backOfficeModel.getBackofficeID()));
            });
        });
    }

    private void updateCounter(Message message) {
        VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleCounter.class);
        backOfficeModel.updateCounter(vehicleCounter);
    }
}
