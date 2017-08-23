package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLE_LIST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ROUTING_KEY_BOOKING_RESPONSE_BOOKING_LIST;

/**
 * the class for recieving a booking list over rabbitMQ with vertx from the booking service
 *
 * @author Stefano Bernagozzi
 */
public class BackofficeBookingsResponseConsumer extends VerticleConsumer {
    private String backofficeId;

    /**
     * @param backofficeId the backoffice id
     */
    public BackofficeBookingsResponseConsumer(String backofficeId) {
        super(RabbitMQ.Exchanges.BOOKING + "." + ROUTING_KEY_BOOKING_RESPONSE_BOOKING_LIST + "." + backofficeId);
        this.backofficeId = backofficeId;
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v -> {
            if (v.succeeded()) {
                futureRetriever.complete();
            } else {
                futureRetriever.fail(v.cause());
            }
        });

        startConsumerWithFuture(RabbitMQ.Exchanges.ANALYTICS,
                RabbitMQ.RoutingKey.ROUTING_KEY_BOOKING_RESPONSE_BOOKING_LIST + "." + backofficeId,
                EventBus.AVAILABLE_ADDRESS_BACKOFFICE_BOOKING_RESPONSE,
                futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendUpdatesToController);
    }

    private void sendUpdatesToController(Message message) {
        VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleCounter.class);
        vertx.eventBus().send(EventBus.BACKOFFICE_CONTROLLER_BOOKINGS, VertxJsonMapper.mapInBodyFrom(vehicleCounter));
    }
}
