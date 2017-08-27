package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.EVENT_BUS_CHANGE_BOOKING_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_CHANGE_BOOKING;


/**
 * Boundary {@linkplain VerticleConsumer} that receives
 * {@linkplain Constants.RabbitMQ.RoutingKey#CHANGE_BOOKING_RESPONSE} messages and redirect them to the proper control.
 *
 * @author Nicola Lasagni
 */
public class ChangeBookingConsumerVerticle extends VerticleConsumer {

    public ChangeBookingConsumerVerticle() {
        super(VEHICLE_SERVICE_QUEUE_CHANGE_BOOKING);
    }

    @Override
    public void start() throws Exception {
        super.start();
        startBookConsumer();
    }

    private void startBookConsumer() throws IOException, TimeoutException {
        startConsumer(false,
                Constants.RabbitMQ.Exchanges.BOOKING,
                Constants.RabbitMQ.RoutingKey.CHANGE_BOOKING_RESPONSE,
                EVENT_BUS_CHANGE_BOOKING_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            sendChangeBookingResponse((JsonObject) msg.body());
        });
    }

    private void sendChangeBookingResponse(JsonObject requestMessage) {
        vertx.eventBus().send(Messages.Booking.CHANGE_BOOKING_RESPONSE, requestMessage);
    }

}
