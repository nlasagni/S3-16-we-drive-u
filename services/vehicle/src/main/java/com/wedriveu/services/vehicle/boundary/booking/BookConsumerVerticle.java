package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.EVENT_BUS_BOOK_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_BOOK;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_SERVICE_BOOK_REQUEST;


/**
 * Vert.x RabbitMQ Consumer that listens for BookingService "book" request.
 *
 * @author Marco Baldassarri on 15/08/2017.
 */
public class BookConsumerVerticle extends VerticleConsumer {

    public BookConsumerVerticle() {
        super(VEHICLE_SERVICE_QUEUE_BOOK);
    }

    @Override
    public void start() throws Exception {
        super.start();
        startBookConsumer();
    }

    private void startBookConsumer() throws IOException, TimeoutException {
        startConsumer(false,
                Constants.RabbitMQ.Exchanges.VEHICLE,
                VEHICLE_SERVICE_BOOK_REQUEST,
                EVENT_BUS_BOOK_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {

            //TODO
            Log.info(this.getClass().getSimpleName(), "Received book vehicle request");

            sendBookingRequestToVehicle((JsonObject) msg.body());
        });
    }

    private void sendBookingRequestToVehicle(JsonObject requestMessage) {
        vertx.deployVerticle(new BookVehicleVerticle(), deployed -> {
            eventBus.send(Messages.Booking.BOOK_REQUEST, requestMessage);
        });

    }

}
