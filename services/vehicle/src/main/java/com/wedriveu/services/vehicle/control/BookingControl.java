package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.entity.BookVehicleResponseWrapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.rabbitmq.message.DriveCommand;
import com.wedriveu.shared.util.Position;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;


/**
 * This Controller serves all the interaction requests with the
 * {@linkplain com.wedriveu.services.vehicle.entity.VehicleStore} database. It handles the database replies
 * by deploying and interacting with other Verticles.
 *
 * @author Marco Baldassarri on 02/08/2017.
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
        vehicleResponseWrapper = (BookVehicleResponseWrapper) message.body();
        vehicleResponse = vehicleResponseWrapper.getResponse();
        if(vehicleResponse.getBooked()) {
            notifyServices();
        } else {
            eventBus.send(Messages.BookingControl.GET_VEHICLE_BOOKING, vehicleResponse);
        }
    }


    private void getVehicleCompleted(Message message) {
        Vehicle vehicle = (Vehicle) message.body();
        fillDriveTimes(vehicle);
        sendStartDrivingCommand();
        notifyServices();
    }

    // Notifies Booking and Analytics Service
    private void notifyServices() {
        eventBus.publish(Messages.BookingControl.PUBLISH_RESULT, vehicleResponse);
    }

    private void sendStartDrivingCommand() {
        DriveCommand driveCommand = new DriveCommand();
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


