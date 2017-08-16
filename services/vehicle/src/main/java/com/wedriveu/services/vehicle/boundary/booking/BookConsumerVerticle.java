package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleRequest;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.EVENT_BUS_BOOK_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_BOOK;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.BOOKING_SERVICE_BOOK_REQUEST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.BOOK_REQUEST;


/**
 * Vert.x RabbitMQ Consumer that listens for Analytics Service requests. It asks for the whole list of Vehicles
 * currently saved in the database file.
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
                BOOKING_SERVICE_BOOK_REQUEST,
                EVENT_BUS_BOOK_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            sendBookingRequestToVehicle((JsonObject) msg.body());
        });
    }

    private void sendBookingRequestToVehicle(JsonObject requestMessage) {
        vertx.deployVerticle(new BookVehicleVerticle(), deployed -> {
            eventBus.send(Messages.Booking.BOOK_REQUEST, requestMessage);
        });

    }

}
