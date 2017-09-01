package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.model.BackOfficeModel;
import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.backoffice.view.BackOfficeView;
import com.wedriveu.services.shared.model.Booking;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * the controller of the backoffice, that manages the messages received and updates the model
 *
 * @author Stefano Bernagozzi
 */
public class BackOfficeController extends AbstractVerticle {
    private BackOfficeModel backOfficeModel;
    private BackOfficeView backOfficeView;
    Vertx vertx;
    private Future futureStart;

    /**
     * the controller class for all the backoffice
     *
     * @param vertx the vertx istance used for starting the verticle
     */
    public BackOfficeController(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        futureStart = futureRetriever;
        init();
        vertx.eventBus().consumer(ConstantsBackOffice.EventBus.BACKOFFICE_CONTROLLER_VEHICLES, this::updateCounter);
        vertx.eventBus().consumer(ConstantsBackOffice.EventBus.BACKOFFICE_CONTROLLER_BOOKINGS, this::manageBookingList);
    }


    private void init() {
        backOfficeModel = new BackOfficeModel();
        backOfficeView = new BackOfficeView();
        backOfficeView.setVisible(true);
        ButtonEventListener buttonEventListener = new ButtonEventListener(backOfficeModel, backOfficeView);
        backOfficeView.addButtonListener(buttonEventListener);
        Future futureModel = backOfficeModel.getFuture();
        List<Future> futures = new ArrayList<>();
        futureModel.setHandler(res -> {
            Future futureVerticle1 = Future.future();
            vertx.deployVerticle(new BackOfficeVehiclesResponseConsumer(
                    backOfficeModel.getBackofficeID(), true),
                    futureVerticle1.completer());
            futures.add(futureVerticle1);
            Future futureVerticle2 = Future.future();
            vertx.deployVerticle(new BackOfficeVehiclesResponseConsumer(
                            backOfficeModel.getBackofficeID(), false),
                    futureVerticle2.completer());
            futures.add(futureVerticle2);
            Future futureVerticle4 = Future.future();
            vertx.deployVerticle(new BackOfficeBookingsRequestPublisher(
                            backOfficeModel.getBackofficeID()),
                    futureVerticle4.completer());
            futures.add(futureVerticle4);
            Future futureVerticle5 = Future.future();
            vertx.deployVerticle(new BackOfficeBookingsResponseConsumer(
                            backOfficeModel.getBackofficeID()),
                    futureVerticle5.completer());
            futures.add(futureVerticle5);
            CompositeFuture.all(futures).setHandler(resAllFutures-> {
                ButtonEventListenerBooking buttonEventListenerBooking = new ButtonEventListenerBooking(vertx);
                backOfficeView.addButtonBookingListener(buttonEventListenerBooking);
                vertx.deployVerticle(new BackOfficeVehicleRequestPublisher(
                                backOfficeModel.getBackofficeID()),fu -> {
                         futureStart.complete();
                });

            });
        });
    }

    private void updateCounter(Message message) {
        VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleCounter.class);
        backOfficeModel.updateCounter(vehicleCounter);
        backOfficeView.updateText(vehicleCounter);
    }

    private void manageBookingList(Message message) {
        try {
            List<Booking> list = VertxJsonMapper.mapFromBodyToList((JsonObject) message.body(), Booking.class);
            MapUtility.generateMap(list);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
