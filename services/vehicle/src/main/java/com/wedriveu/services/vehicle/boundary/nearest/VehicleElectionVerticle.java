package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.VehicleResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Calendar;
import java.util.Date;

import static com.wedriveu.shared.util.Constants.USERNAME;
import static com.wedriveu.shared.util.Constants.VEHICLE;
import static com.wedriveu.shared.util.Constants.Vehicle.SPEED;

/**
 * This Verticle uses RabbitMQ Vertx.x library to publish the chosen vehicle to the client.
 *
 * @author Marco Baldassarri on 4/08/2017.
 */
public class VehicleElectionVerticle extends VerticlePublisher {

    private static final long HOUR_IN_MILLISECONDS = 3600000;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.VehicleStore.GET_VEHICLE_COMPLETED_NEAREST, this::sendVehicleToUser);
        super.start(startFuture);
    }

    private void sendVehicleToUser(Message message) {
        JsonObject body = (JsonObject) message.body();
        String username = body.getString(USERNAME);
        body.remove(USERNAME);
        publishToUser(username, createResponse(body));
    }

    private void publishToUser(String username, JsonObject dataToUser) {
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE, username),
                dataToUser, onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(VehicleElectionVerticle.class.getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }

    private JsonObject createResponse(JsonObject content) {
        Vehicle vehicle = new JsonObject(content.getString(VEHICLE)).mapTo(Vehicle.class);
        VehicleResponse vehicleResponse = new VehicleResponse();
        vehicleResponse.setLicensePlate(vehicle.getLicensePlate());
        vehicleResponse.setVehicleName(vehicle.getName());
        vehicleResponse.setDescription(vehicle.getDescription());
        vehicleResponse.setPictureURL(vehicle.getImageUrl());
        vehicleResponse.setNotEligibleVehicleFound(vehicle.getNotEligibleVehicleFound());
        Double speed = content.getDouble(SPEED);
        Double distanceToUser = content.getDouble(Constants.Trip.DISTANCE_TO_USER);
        Double totalDistance = content.getDouble(Constants.Trip.TOTAL_DISTANCE);
        if (speed != null && distanceToUser != null && totalDistance != null) {
            Calendar today = Calendar.getInstance();
            vehicleResponse.setArriveAtUserTime(today.getTimeInMillis() + getTimeInMilliseconds(distanceToUser, speed));
            vehicleResponse.setArriveAtDestinationTime(today.getTimeInMillis() + getTimeInMilliseconds(totalDistance, speed));
        }
        return VertxJsonMapper.mapInBodyFrom(vehicleResponse);
    }

    private long getTimeInMilliseconds(double distance, double speed) {
        double hourTime = distance / speed;
        return (long) hourTime * HOUR_IN_MILLISECONDS;
    }

}
