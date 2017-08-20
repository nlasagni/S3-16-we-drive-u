package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.entity.BookVehicleResponseWrapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.rabbitmq.message.DriveCommand;
import com.wedriveu.shared.util.Log;
import com.wedriveu.shared.util.Position;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Date;


/**
 * This Controller serves some interaction requests with the
 * {@linkplain com.wedriveu.services.vehicle.entity.VehicleStore} database. It handles the database replies
 * by deploying and interacting with other Verticles.
 * BookingControl calculates the drive times to the user and to the destinations, undeploys short-life verticles
 * like {@linkplain com.wedriveu.services.vehicle.boundary.booking.BookVehicleVerticle} and send back data
 * to the publishers related to the BookingService and the booking process.
 *
 * @author Marco Baldassarri on 17/08/2017.
 */
public class BookingControl extends AbstractVerticle {

    private static final long HOUR_IN_MILLISECONDS = 3600000;
    private EventBus eventBus;
    private BookVehicleResponse vehicleResponse;
    private BookVehicleResponseWrapper vehicleResponseWrapper;

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.Booking.BOOK_RESPONSE, this::handleBookResponse);
        eventBus.consumer(Messages.Booking.UNDEPLOY, this::undeployBookVehicleVerticle);
        eventBus.consumer(Messages.VehicleStore.GET_VEHICLE_COMPLETED_BOOKING, this::getVehicleCompleted);
    }

    private void handleBookResponse(Message message) {
        vehicleResponseWrapper = VertxJsonMapper.mapTo((JsonObject) message.body(), BookVehicleResponseWrapper.class);
        vehicleResponse = vehicleResponseWrapper.getResponse();
        if (!vehicleResponse.getBooked()) {
            notifyBookingService();
        } else {
            eventBus.send(Messages.BookingControl.GET_VEHICLE_BOOKING, VertxJsonMapper.mapFrom(vehicleResponse));
        }
    }

    private void getVehicleCompleted(Message message) {
        Vehicle vehicle = VertxJsonMapper.mapTo((JsonObject) message.body(), Vehicle.class);
        fillDriveTimes(vehicle);
        sendStartDrivingCommand();
        notifyBookingService();
    }

    private void notifyBookingService() {
        eventBus.send(Messages.BookingControl.PUBLISH_RESULT, VertxJsonMapper.mapFrom(vehicleResponse));
    }

    private void sendStartDrivingCommand() {
        DriveCommand driveCommand = new DriveCommand();
        driveCommand.setLicensePlate(vehicleResponseWrapper.getResponse().getLicensePlate());
        driveCommand.setUserPosition(vehicleResponseWrapper.getUserPosition());
        driveCommand.setDestinationPosition(vehicleResponseWrapper.getDestinationPosition());

        //TODO
        Log.info(this.getClass().getSimpleName(), "sendStartDrivingCommand");

        eventBus.send(Messages.BookingControl.START_DRIVING, VertxJsonMapper.mapInBodyFrom(driveCommand));

    }

    private void fillDriveTimes(Vehicle vehicle) {
        Position userPosition = vehicleResponseWrapper.getUserPosition();
        Position destinationPosition = vehicleResponseWrapper.getDestinationPosition();
        Position vehiclePosition = vehicle.getPosition();
        double distanceToUser = Position.getDistanceInKm(userPosition, vehiclePosition);
        double distanceToDestination = Position.getDistanceInKm(destinationPosition, vehiclePosition);
        long timeMillisUser = getTimeInMilliseconds(distanceToUser);
        long timeMillisDestination = getTimeInMilliseconds(distanceToDestination);
        vehicleResponse.setDriveTimeToUser(timeMillisUser);
        vehicleResponse.setDriveTimeToDestination(timeMillisDestination);
    }

    private void undeployBookVehicleVerticle(Message message) {
        String deploymentId = (String) message.body();
        if (deploymentId != null && !deploymentId.isEmpty()) {
            vertx.undeploy(deploymentId);
        }
    }

    private long getTimeInMilliseconds(double distance) {
        double hourTime = distance / vehicleResponse.getSpeed();
        return (long) hourTime * HOUR_IN_MILLISECONDS;
    }
}


