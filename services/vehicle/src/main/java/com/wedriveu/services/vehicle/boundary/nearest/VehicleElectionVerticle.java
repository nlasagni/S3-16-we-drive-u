package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.services.vehicle.util.Time;
import com.wedriveu.shared.rabbitmq.message.VehicleResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Date;

import static com.wedriveu.shared.util.Constants.USERNAME;
import static com.wedriveu.shared.util.Constants.VEHICLE;

/**
 * This Verticle uses RabbitMQ Vertx.x library to publish the chosen vehicle to the client.
 *
 * @author Marco Baldassarri on 4/08/2017.
 * @author Nicola Lasagni
 */
public class VehicleElectionVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.VehicleStore.GET_VEHICLE_COMPLETED_NEAREST,
                this::sendVehicleToUser);
        vertx.eventBus().consumer(Messages.VehicleSubstitution.SEND_SUBSTITUTION_VEHICLE_TO_USER,
                this::sendSubstitutionVehicleToUser);
        super.start(startFuture);
    }

    private void sendVehicleToUser(Message message) {
        sendVehicle(message, Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE);
    }

    private void sendSubstitutionVehicleToUser(Message message) {

        sendVehicle(message, Constants.RabbitMQ.RoutingKey.VEHICLE_SUBSTITUTION);
    }

    private void sendVehicle(Message message, String routingKey) {
        JsonObject body = (JsonObject) message.body();
        String username = body.getString(USERNAME);
        publishToUser(username, routingKey, createResponse(body));
    }

    private void publishToUser(String username, String routingKey, JsonObject dataToUser) {
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(routingKey, username),
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
        Double speed = content.getDouble(Messages.Trip.SPEED);
        Double distanceToUser = content.getDouble(Messages.Trip.DISTANCE_TO_USER);
        Double totalDistance = content.getDouble(Messages.Trip.TOTAL_DISTANCE);
        if (speed != null && distanceToUser != null && totalDistance != null) {
            Date date = new Date();
            vehicleResponse.setArriveAtUserTime(
                    date.getTime() + Time.getDriveTimeInMilliseconds(distanceToUser, speed));
            vehicleResponse.setArriveAtDestinationTime(
                    date.getTime() + Time.getDriveTimeInMilliseconds(totalDistance, speed));
        }
        return VertxJsonMapper.mapInBodyFrom(vehicleResponse);
    }

}
