package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.entity.SubstitutionCheck;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.FindBookingPositionsRequest;
import com.wedriveu.shared.rabbitmq.message.FindBookingPositionsResponse;
import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

/**
 * This {@linkplain AbstractVerticle} manages a {@linkplain Vehicle} {@linkplain SubstitutionCheck},
 * so it gets the {@linkplain com.wedriveu.services.shared.model.Booking} positions associated to the
 * {@linkplain Vehicle} to be substituted through the proper boundary that is the
 * {@linkplain com.wedriveu.services.vehicle.boundary.booking.GetBookingPositionsVerticle}, makes its checks
 * and sends back the result to the {@linkplain SubstitutionControl}.
 *
 * @author Nicola Lasagni on 25/08/2017.
 */
public class SubstitutionChecker extends AbstractVerticle {

    private String id;
    private EventBus eventBus;
    private VehicleUpdate vehicleUpdate;
    private MessageConsumer getPositionConsumer;
    private boolean substitutionNeeded;

    SubstitutionChecker(String id) {
        this.id = id;
    }

    @Override
    public void start() throws Exception {
        eventBus = vertx.eventBus();
        eventBus.consumer(String.format(Messages.VehicleSubstitution.CHECK_FOR_SUBSTITUTION, id) , msg -> {
            vehicleUpdate = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), VehicleUpdate.class);
            getVehicleToCheck();
        });
        eventBus.consumer(String.format(Messages.VehicleStore.GET_VEHICLE_FOR_SUBSTITUTION_COMPLETED, id),
                this::checkForSubstitutionAndUpdate);
    }

    private void getVehicleToCheck() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Messages.SENDER_ID, id);
        jsonObject.put(Messages.Trip.LICENSE_PLATE, vehicleUpdate.getLicense());
        eventBus.send(Messages.VehicleSubstitution.GET_VEHICLE_FOR_SUBSTITUTION, jsonObject);
    }

    private void checkForSubstitutionAndUpdate(Message message) {
        JsonObject body = (JsonObject) message.body();
        if (body == null) {
            sendCheckFailed();
        } else {
            Vehicle vehicle = VertxJsonMapper.mapTo(body, Vehicle.class);
            substitutionNeeded =
                    Constants.Vehicle.STATUS_BROKEN_STOLEN.equals(vehicleUpdate.getStatus()) &&
                            Constants.Vehicle.STATUS_BOOKED.equals(vehicle.getStatus());
            if (substitutionNeeded) {
                sendGetBookingPositionsRequest();
            } else {
                SubstitutionCheck check =
                        createSubstitutionCheck(false,
                                vehicleUpdate,
                                null,
                                null);
                sendCheckCompleted(check);
            }
            eventBus.send(Messages.VehicleStore.UPDATE_VEHICLE_STATUS,
                    VertxJsonMapper.mapInBodyFrom(vehicleUpdate));
        }
    }

    private void sendGetBookingPositionsRequest() {
        getPositionConsumer = eventBus.consumer(
                String.format(Messages.Booking.GET_BOOKING_POSITIONS_COMPLETED, vehicleUpdate.getLicense()),
                this::sendCheckResponse);
        FindBookingPositionsRequest request = new FindBookingPositionsRequest();
        request.setUsername(vehicleUpdate.getUsername());
        eventBus.send(Messages.Booking.GET_BOOKING_POSITIONS, VertxJsonMapper.mapFrom(request));
    }

    private void sendCheckResponse(Message message) {
        getPositionConsumer.unregister();
        FindBookingPositionsResponse response =
                VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), FindBookingPositionsResponse.class);
        if (!response.isSuccessful()) {
            sendCheckFailed();
        } else {
            Position sourcePosition =
                    vehicleUpdate.isUserOnBoard() ? vehicleUpdate.getPosition() : response.getUserPosition();
            SubstitutionCheck check =
                    createSubstitutionCheck(substitutionNeeded,
                            vehicleUpdate,
                            sourcePosition,
                            response.getDestinationPosition());
            sendCheckCompleted(check);
        }
    }

    private void sendCheckCompleted(SubstitutionCheck substitutionCheck) {
        eventBus.send(Messages.VehicleSubstitution.CHECK_FOR_SUBSTITUTION_COMPLETED,
                VertxJsonMapper.mapFrom(substitutionCheck));
    }

    private void sendCheckFailed() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.USERNAME, vehicleUpdate.getUsername());
        eventBus.send(Messages.VehicleSubstitution.CHECK_FOR_SUBSTITUTION_FAILED, jsonObject);
    }

    private SubstitutionCheck createSubstitutionCheck(boolean substitutionNeeded,
                                                      VehicleUpdate vehicleUpdate,
                                                      Position sourcePosition,
                                                      Position destinationPosition) {
        return new SubstitutionCheck(id,
                substitutionNeeded,
                vehicleUpdate,
                sourcePosition,
                destinationPosition);
    }

}
