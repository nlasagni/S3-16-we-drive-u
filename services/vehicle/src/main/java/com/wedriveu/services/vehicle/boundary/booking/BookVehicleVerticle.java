package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.entity.BookVehicleResponseWrapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.VehicleReservationRequest;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.EVENT_BUS_BOOK_VEHICLE_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_BOOK_VEHICLE;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.BOOK_REQUEST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.BOOK_RESPONSE;


/**
 * Vert.x RabbitMQ Publisher and Consumer. Sends a Vehicle the booking requests and
 * listens for Vehicle response.
 *
 * @author Marco Baldassarri on 16/08/2017.
 */
public class BookVehicleVerticle extends VerticleConsumer {

    private static final String TAG = BookVehicleVerticle.class.getSimpleName();
    private static final String ERROR_MESSAGE = "Error starting BookVehicle consumer";
    private BookVehicleRequest vehicleRequest;
    private RabbitMQClient client;

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
        publishBookRequest();
    }

    private void publishBookRequest() {
        startRabbitMQClient(clientStarted -> {
            VehicleReservationRequest requestData = new VehicleReservationRequest();
            requestData.setUsername(vehicleRequest.getUsername());
            client.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                    String.format(BOOK_REQUEST,
                            vehicleRequest.getLicencePlate()),
                    VertxJsonMapper.mapInBodyFrom(requestData), onPublish -> {
                    });
        });
    }

    private void startRabbitMQClient(Handler<AsyncResult<Void>> resultHandler) {
        if (!client.isConnected()) {
            client = RabbitMQClientFactory.createClient(vertx);
            client.start(resultHandler);
        }
    }

    private void startBookVehicleConsumer() throws IOException, TimeoutException {
        startConsumer(false,
                Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(BOOK_RESPONSE, vehicleRequest.getLicencePlate()),
                EVENT_BUS_BOOK_VEHICLE_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            handleVehicleBookingResponse((JsonObject) msg.body());
        });
    }

    private void handleVehicleBookingResponse(JsonObject responseJson) {
        BookVehicleResponseWrapper response =
                VertxJsonMapper.mapFromBodyTo(responseJson, BookVehicleResponseWrapper.class);
        response.getResponse().setLicencePlate(vehicleRequest.getLicencePlate());
        response.setUserPosition(vehicleRequest.getUserPosition());
        response.setDestinationPosition(vehicleRequest.getDestinationPosition());
        JsonObject data = VertxJsonMapper.mapFrom(response);
        eventBus.send(Messages.Booking.BOOK_RESPONSE, data);
        eventBus.send(Messages.Booking.UNDEPLOY, deploymentID());
    }

}
