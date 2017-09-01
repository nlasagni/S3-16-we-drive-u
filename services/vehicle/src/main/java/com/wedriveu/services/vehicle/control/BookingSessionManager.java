package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.entity.BookVehicleResponseWrapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.services.vehicle.util.Time;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.rabbitmq.message.DriveCommand;
import com.wedriveu.shared.util.Log;
import com.wedriveu.shared.util.Position;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Control {@linkplain AbstractVerticle} that manages a {@linkplain Vehicle} booking session,
 * so it sends messages to confirm the vehicle {@linkplain BookVehicleResponse} and sends to the vehicle
 * the {@linkplain DriveCommand} through the proper boundaries, that are
 * {@linkplain com.wedriveu.services.vehicle.boundary.booking.BookPublisherVerticle} and
 * {@linkplain com.wedriveu.services.vehicle.boundary.booking.StartDrivingPublisherVerticle}.
 *
 * @author Nicola Lasagni on 26/08/2017.
 */
public class BookingSessionManager extends AbstractVerticle {

    private String id;
    private BookVehicleResponseWrapper vehicleResponseWrapper;
    private BookVehicleResponse vehicleResponse;
    private EventBus eventBus;

    BookingSessionManager(String id) {
        this.id = id;
    }

    @Override
    public void start() throws Exception {
        eventBus = vertx.eventBus();
        eventBus.consumer(Messages.Booking.START_VEHICLE, this::handleBookResponse);
        eventBus.consumer(String.format(Messages.VehicleStore.GET_VEHICLE_COMPLETED_BOOKING, id),
                this::getVehicleCompleted);
    }

    private void handleBookResponse(Message message) {
        vehicleResponseWrapper =
                VertxJsonMapper.mapTo((JsonObject) message.body(), BookVehicleResponseWrapper.class);
        vehicleResponse = vehicleResponseWrapper.getResponse();
        if (!vehicleResponse.getBooked()) {
            notifyBookingService();
        } else {
            JsonObject jsonObject = VertxJsonMapper.mapFrom(vehicleResponse);
            jsonObject.put(Messages.SENDER_ID, id);
            eventBus.send(Messages.BookingControl.GET_VEHICLE_BOOKING, jsonObject);
        }
    }

    private void getVehicleCompleted(Message message) {
        JsonObject body = (JsonObject) message.body();
        if (body != null) {
            Vehicle vehicle = VertxJsonMapper.mapTo(body, Vehicle.class);
            fillDriveTimes(vehicle);
            sendStartDrivingCommand();
            notifyBookingService();
        }
        eventBus.send(Messages.Booking.UNDEPLOY, deploymentID());
    }

    private void notifyBookingService() {
        eventBus.send(Messages.BookingControl.PUBLISH_RESULT, VertxJsonMapper.mapFrom(vehicleResponse));
    }

    private void sendStartDrivingCommand() {
        DriveCommand driveCommand = new DriveCommand();
        driveCommand.setLicensePlate(vehicleResponseWrapper.getResponse().getLicensePlate());
        driveCommand.setUserPosition(vehicleResponseWrapper.getUserPosition());
        driveCommand.setDestinationPosition(vehicleResponseWrapper.getDestinationPosition());
        eventBus.send(Messages.BookingControl.START_DRIVING, VertxJsonMapper.mapInBodyFrom(driveCommand));

    }

    private void fillDriveTimes(Vehicle vehicle) {
        Position userPosition = vehicleResponseWrapper.getUserPosition();
        Position destinationPosition = vehicleResponseWrapper.getDestinationPosition();
        Position vehiclePosition = vehicle.getPosition();
        double distanceToUser = Position.getDistanceInKm(userPosition, vehiclePosition);
        double distanceToDestination = Position.getDistanceInKm(destinationPosition, vehiclePosition);
        long timeMillisUser = Time.getDriveTimeInMilliseconds(distanceToUser, vehicleResponse.getSpeed());
        long timeMillisDestination = Time.getDriveTimeInMilliseconds(distanceToDestination, vehicleResponse.getSpeed());
        vehicleResponse.setDriveTimeToUser(timeMillisUser);
        vehicleResponse.setDriveTimeToDestination(timeMillisDestination);
    }

}
