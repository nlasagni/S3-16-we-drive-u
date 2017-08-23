package com.wedriveu.backoffice.controller;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.BackofficeBookingListRequest;
import com.wedriveu.shared.rabbitmq.message.FindBookingPositionsRequest;
import com.wedriveu.shared.rabbitmq.message.FindBookingPositionsResponse;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.backoffice.util.EventBus.BACKOFFICE_BOOKING_LIST_REQUEST_CONTROLLER;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ROUTING_KEY_BOOKING_REQUEST_BOOKING_LIST;

/**
 * the class for sending a request of the booking list over rabbitMQ with vertx
 *
 * @author Stefano Bernagozzi
 */
public class BackofficeBookingsRequestPublisher extends VerticlePublisher {
    private String backofficeId;

    public BackofficeBookingsRequestPublisher(String backofficeId) {
        this.backofficeId = backofficeId;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future future = Future.future();
        super.start(future);
        future.setHandler(res -> {
            startFuture.complete();
            startBookingRetriever();
        });
    }

    private void startBookingRetriever() {
        vertx.eventBus().consumer(BACKOFFICE_BOOKING_LIST_REQUEST_CONTROLLER,this::requestVehicleCounterToBookingService);
    }

    private void requestVehicleCounterToBookingService(Message message) {
        JsonObject dataToUser = new JsonObject();
        BackofficeBookingListRequest backofficeBookingListRequest = new BackofficeBookingListRequest();
        backofficeBookingListRequest.setBackofficeId(backofficeId);
        dataToUser.put(Constants.EventBus.BODY, VertxJsonMapper.mapInBodyFrom(backofficeBookingListRequest));
        publish(Constants.RabbitMQ.Exchanges.BOOKING,ROUTING_KEY_BOOKING_REQUEST_BOOKING_LIST, dataToUser, published -> {
        });
    }

}