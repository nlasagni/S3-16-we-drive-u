package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.entity.BookVehicleResponseWrapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.rabbitmq.message.VehicleReservationRequest;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.EVENT_BUS_BOOK_VEHICLE_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_BOOK_VEHICLE;
import static com.wedriveu.services.vehicle.rabbitmq.Messages.Booking.BOOK_RESPONSE;
import static com.wedriveu.services.vehicle.rabbitmq.Messages.VehicleSubstitution.BOOK_FOR_SUBSTITUTION_RESPONSE;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.BOOK_VEHICLE_REQUEST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.BOOK_VEHICLE_RESPONSE;


/**
 * Vert.x RabbitMQ Publisher and Consumer. Sends a Vehicle the booking requests and
 * listens for Vehicle response.
 *
 * @author Marco Baldassarri on 16/08/2017.
 */
public class BookVehicleVerticle extends VerticleConsumer {

    private static final String TAG = BookVehicleVerticle.class.getSimpleName();
    private static final String ERROR_MESSAGE = "Error starting BookVehicle consumer";

    private String id;
    private boolean forSubstitution;
    private BookVehicleRequest vehicleRequest;

    /**
     * Instantiates a new Book vehicle verticle.
     *
     * @param id              the id of this verticle
     * @param forSubstitution indicates if this verticle should handle a substitution case
     */
    public BookVehicleVerticle(String id, boolean forSubstitution) {
        super(String.format(VEHICLE_SERVICE_QUEUE_BOOK_VEHICLE, id));
        this.id = id;
        this.forSubstitution = forSubstitution;
    }

    @Override
    public void start() throws Exception {
        super.start();
        eventBus.consumer(String.format(Messages.Booking.BOOK_REQUEST, id), this::handleBookingRequest);
    }

    private void handleBookingRequest(Message message) {
        vehicleRequest = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), BookVehicleRequest.class);
        try {
            Future<Void> future = Future.future();
            future.setHandler(onStart -> publishBookRequest());
            startBookVehicleConsumer(future);
        } catch (IOException | TimeoutException e) {
            Log.error(TAG, ERROR_MESSAGE, e);
        }
    }

    private void publishBookRequest() {
        VehicleReservationRequest requestData = new VehicleReservationRequest();
        requestData.setUsername(vehicleRequest.getUsername());
        client.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(BOOK_VEHICLE_REQUEST,
                        vehicleRequest.getLicencePlate()),
                VertxJsonMapper.mapInBodyFrom(requestData), onPublish -> {
                });
    }

    private void startBookVehicleConsumer(Future future) throws IOException, TimeoutException {
        startConsumerWithFuture(
                Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(BOOK_VEHICLE_RESPONSE, vehicleRequest.getUsername()),
                EVENT_BUS_BOOK_VEHICLE_ADDRESS + id,
                future);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> handleVehicleBookingResponse((JsonObject) msg.body()));
    }

    private void handleVehicleBookingResponse(JsonObject responseJson) {
        BookVehicleResponse response = VertxJsonMapper.mapFromBodyTo(responseJson, BookVehicleResponse.class);
        BookVehicleResponseWrapper wrapper = new BookVehicleResponseWrapper();
        wrapper.setUsername(vehicleRequest.getUsername());
        wrapper.setResponse(response);
        wrapper.getResponse().setLicensePlate(vehicleRequest.getLicencePlate());
        wrapper.setUserPosition(vehicleRequest.getUserPosition());
        wrapper.setDestinationPosition(vehicleRequest.getDestinationPosition());
        JsonObject data = VertxJsonMapper.mapFrom(wrapper);

        String responseAddress = BOOK_RESPONSE;
        String undeployAddress = Messages.Booking.UNDEPLOY;
        if (forSubstitution) {
            responseAddress = BOOK_FOR_SUBSTITUTION_RESPONSE;
            undeployAddress = Messages.VehicleSubstitution.UNDEPLOY;
        }
        eventBus.send(responseAddress, data);
        eventBus.send(undeployAddress, deploymentID());
    }

}
