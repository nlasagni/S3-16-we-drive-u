package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.boundary.booking.BookVehicleVerticle;
import com.wedriveu.services.vehicle.entity.BookVehicleResponseWrapper;
import com.wedriveu.services.vehicle.entity.SubstitutionCheck;
import com.wedriveu.services.vehicle.entity.SubstitutionRequest;
import com.wedriveu.services.vehicle.entity.VehicleResponseCanDrive;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.*;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.wedriveu.shared.util.Constants.USERNAME;
import static com.wedriveu.shared.util.Constants.VEHICLE;

/**
 * @author Nicola Lasagni on 25/08/2017.
 */
public class SubstitutionControl extends AbstractVerticle {

    private EventBus eventBus;
    private Map<String, SubstitutionRequest> substitutionRequests;

    @Override
    public void start() throws Exception {
        eventBus = vertx.eventBus();
        substitutionRequests = new HashMap<>();
        eventBus.consumer(Messages.VehicleSubstitution.START_SUBSTITUTION,
                this::checkAndStartVehicleSubstitution);
        eventBus.consumer(Messages.VehicleSubstitution.CHECK_FOR_SUBSTITUTION_COMPLETED,
                this::findSubstitutionVehicle);
        eventBus.consumer(Messages.VehicleSubstitution.CHECK_FOR_SUBSTITUTION_FAILED,
                this::abortSubstitution);
        eventBus.consumer(Messages.VehicleStore.GET_NEAREST_VEHICLE_FOR_SUBSTITUTION_COMPLETED,
                this::bookSubstitutionVehicle);
        eventBus.consumer(Messages.VehicleSubstitution.BOOK_FOR_SUBSTITUTION_RESPONSE,
                this::handleBookSubstitutionResponse);
        eventBus.consumer(Messages.Booking.CHANGE_BOOKING_RESPONSE,
                this::handleChangeBookingResponse);
        eventBus.consumer(Messages.VehicleSubstitution.UNDEPLOY,
                this::undeployVerticle);
    }

    private void checkAndStartVehicleSubstitution(Message message) {
        UUID randomId = UUID.randomUUID();
        vertx.deployVerticle(new SubstitutionChecker(randomId.toString()), onDeploy ->
                eventBus.send(Messages.VehicleSubstitution.CHECK_FOR_SUBSTITUTION, message.body())
        );
    }

    private void findSubstitutionVehicle(Message message) {
        SubstitutionCheck substitutionCheck =
                VertxJsonMapper.mapTo((JsonObject) message.body(), SubstitutionCheck.class);
        if (substitutionCheck.isNeeded()) {
            SubstitutionRequest request =
                    new SubstitutionRequest(substitutionCheck,
                            null,
                            null,
                            new ArrayList<>());
            substitutionRequests.put(substitutionCheck.getUpdateToService().getUsername(), request);
            eventBus.send(Messages.VehicleSubstitution.GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION,
                    JsonObject.mapFrom(request));
        }
        vertx.undeploy(substitutionCheck.getCheckerId());
    }

    private void abortSubstitution(Message message) {
        JsonObject body = (JsonObject) message.body();
        String username = body.getString(Constants.USERNAME);
        abortSubstitution(username);
    }

    private void abortSubstitution(String username) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(USERNAME, username);
        substitutionRequests.remove(username);
        eventBus.send(Messages.VehicleSubstitution.NO_VEHICLE_FOR_SUBSTITUTION, jsonObject);
    }

    private void bookSubstitutionVehicle(Message message) {
        JsonObject body = (JsonObject) message.body();
        String username = body.getString(USERNAME);
        Vehicle vehicle = new JsonObject((String) body.remove(VEHICLE)).mapTo(Vehicle.class);
        VehicleResponseCanDrive canDrive = VertxJsonMapper.mapTo(body, VehicleResponseCanDrive.class);
        SubstitutionRequest substitutionRequest = substitutionRequests.get(username);
        SubstitutionRequest completeSubstitutionRequest =
                new SubstitutionRequest(substitutionRequest.getSubstitutionCheck(),
                        vehicle,
                        canDrive,
                        substitutionRequest.getVehicleList());
        substitutionRequests.put(username, completeSubstitutionRequest);


        BookVehicleRequest request = new BookVehicleRequest();
        request.setLicencePlate(vehicle.getLicensePlate());
        request.setUsername(username);
        request.setUserPosition(substitutionRequest.getSubstitutionCheck().getSourcePosition());
        request.setDestinationPosition(substitutionRequest.getSubstitutionCheck().getDestinationPosition());
        String id = UUID.randomUUID().toString();
        vertx.deployVerticle(new BookVehicleVerticle(id, true), deployed ->
            eventBus.send(String.format(Messages.Booking.BOOK_REQUEST, id), VertxJsonMapper.mapInBodyFrom(request))
        );
    }

    private void handleBookSubstitutionResponse(Message message) {
        BookVehicleResponseWrapper wrapper =
                VertxJsonMapper.mapTo((JsonObject) message.body(), BookVehicleResponseWrapper.class);
        BookVehicleResponse response = wrapper.getResponse();
        SubstitutionRequest request = substitutionRequests.get(wrapper.getUsername());
        if (!response.getBooked()) {
            retrySubstitution(request);
        } else {
            changeBooking(request, wrapper);
        }
    }

    private void retrySubstitution(SubstitutionRequest substitutionRequest) {
        eventBus.send(Messages.VehicleSubstitution.GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION,
                JsonObject.mapFrom(substitutionRequest));
    }

    private void changeBooking(SubstitutionRequest substitutionRequest,
                               BookVehicleResponseWrapper wrapper) {
        SubstitutionCheck substitutionCheck = substitutionRequest.getSubstitutionCheck();
        BookVehicleResponse response = wrapper.getResponse();
        ChangeBookingRequest changeBookingRequest = new ChangeBookingRequest();
        changeBookingRequest.setNewLicensePlate(response.getLicensePlate());
        changeBookingRequest.setUsername(substitutionCheck.getUpdateToService().getUsername());
        eventBus.send(Messages.Booking.CHANGE_BOOKING_REQUEST,
                VertxJsonMapper.mapFrom(changeBookingRequest));
    }

    private void handleChangeBookingResponse(Message message) {
        ChangeBookingResponse response =
                VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), ChangeBookingResponse.class);
        if (!response.isSuccessful()) {
            abortSubstitution(response.getUsername());
        } else {
            SubstitutionRequest substitutionRequest = substitutionRequests.remove(response.getUsername());
            sendSubstitutionVehicleToUser(response, substitutionRequest);
            sendDriveCommandToVehicle(response, substitutionRequest);
        }
    }

    private void sendSubstitutionVehicleToUser(ChangeBookingResponse response,
                                               SubstitutionRequest substitutionRequest) {
        Vehicle vehicle = substitutionRequest.getSubstitutionVehicle();
        VehicleResponseCanDrive canDrive = substitutionRequest.getSubstitutionVehicleResponseCanDrive();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.USERNAME, response.getUsername());
        jsonObject.put(VEHICLE, JsonObject.mapFrom(vehicle).toString());
        jsonObject.put(Constants.Trip.DISTANCE_TO_USER, canDrive.getDistanceToUser());
        jsonObject.put(Constants.Trip.TOTAL_DISTANCE, canDrive.getTotalDistance());
        jsonObject.put(Constants.Vehicle.SPEED, canDrive.getVehicleSpeed());
        eventBus.send(Messages.VehicleSubstitution.SEND_SUBSTITUTION_VEHICLE_TO_USER, jsonObject);
    }

    private void sendDriveCommandToVehicle(ChangeBookingResponse response,
                                           SubstitutionRequest substitutionRequest) {
        DriveCommand driveCommand = new DriveCommand();
        driveCommand.setLicensePlate(response.getLicencePlate());
        driveCommand.setUserPosition(substitutionRequest.getSubstitutionCheck().getSourcePosition());
        driveCommand.setDestinationPosition(substitutionRequest.getSubstitutionCheck().getDestinationPosition());
        eventBus.send(Messages.BookingControl.START_DRIVING, VertxJsonMapper.mapInBodyFrom(driveCommand));
    }

    private void undeployVerticle(Message<String> message) {
        vertx.undeploy(message.body());
    }

}
