package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.*;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLE_REQUEST_ALL;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.BOOK_REQUEST;


/**
 * Vert.x RabbitMQ Publisher and Consumer. Sends a Vehicle the booking requests and
 * listens for Vehicle response.
 *
 * @author Marco Baldassarri on 16/08/2017.
 */
public class BookVehicleVerticle extends VerticleConsumer {

    private BookVehicleRequest vehicleRequest;
    private static final String TAG = BookVehicleVerticle.class.getSimpleName();
    private static final String ERROR_MESSAGE = "Error starting BookVehicle consumer";

    public BookVehicleVerticle() {
        super(VEHICLE_SERVICE_QUEUE_BOOK_VEHICLE);
    }

    @Override
    public void start() throws Exception {
        super.start();
        eventBus.consumer(Messages.VehicleStore.AVAILABLE_COMPLETED, this::handleBookingRequest);
    }

    private void handleBookingRequest(Message message) {
        vehicleRequest = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), BookVehicleRequest.class);
        try {
            startBookVehicleConsumer();
        } catch (IOException e) {
            Log.error(TAG, ERROR_MESSAGE, e);
        } catch (TimeoutException e) {
            Log.error(TAG, ERROR_MESSAGE, e);
        }
        //TODO write publisher
    }


    private void startBookVehicleConsumer() throws IOException, TimeoutException {
        startConsumer(false,
                Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(BOOK_REQUEST, vehicleRequest.getLicencePlate()),
                EVENT_BUS_BOOK_VEHICLE_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            handleVehicleBookingResponse((JsonObject) msg.body());
        });
    }

    private void handleVehicleBookingResponse(JsonObject responseJson) {
        BookVehicleResponse response = VertxJsonMapper.mapFromBodyTo(responseJson, BookVehicleResponse.class);
        eventBus.send(Messages.Booking.BOOK_RESPONSE, response);
    }

}
