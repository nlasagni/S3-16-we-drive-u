package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.services.shared.model.Booking;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static com.wedriveu.shared.util.Constants.RabbitMQ;

/**
 * the class for recieving a booking list over rabbitMQ with vertx from the booking service
 *
 * @author Stefano Bernagozzi
 */
public class BackOfficeBookingsResponseConsumer extends VerticleConsumer {
    private String backofficeId;

    /**
     * @param backofficeId the backoffice id
     */
    public BackOfficeBookingsResponseConsumer(String backofficeId) {
        super(String.format(Constants.FORMAT_WITH_DOT,
                RabbitMQ.Exchanges.BOOKING,
                String.format(RabbitMQ.RoutingKey.BOOKING_RESPONSE_BOOKING_LIST, backofficeId)));
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

        startConsumerWithFuture(RabbitMQ.Exchanges.BOOKING,
                String.format(RabbitMQ.RoutingKey.BOOKING_RESPONSE_BOOKING_LIST, backofficeId),
                ConstantsBackOffice.EventBus.AVAILABLE_ADDRESS_BACKOFFICE_BOOKING_RESPONSE,
                futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendUpdatesToController);
    }

    private void sendUpdatesToController(Message message) {
        try {
            List<Booking> bookings = VertxJsonMapper.mapFromBodyToList((JsonObject) message.body(), Booking.class);
            vertx.eventBus().send(ConstantsBackOffice.EventBus.BACKOFFICE_CONTROLLER_BOOKINGS, VertxJsonMapper.mapListInBodyFrom(bookings));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
