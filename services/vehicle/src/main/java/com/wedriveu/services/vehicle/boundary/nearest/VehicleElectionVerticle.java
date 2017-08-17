package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.shared.rabbitmq.message.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.EventBus.BODY;
import static com.wedriveu.shared.util.Constants.USERNAME;
import static com.wedriveu.shared.util.Constants.VEHICLE;

/**
 * This Verticle uses RabbitMQ Vertx.x library to publish the chosen vehicle to the client.
 *
 * @author Marco Baldassarri on 4/08/2017.
 */
public class VehicleElectionVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.VehicleStore.GET_VEHICLE_COMPLETED, this::sendVehicleToUser);
        super.start(startFuture);
    }

    private void sendVehicleToUser(Message message) {
        JsonObject dataToUser = new JsonObject();
        JsonObject body = (JsonObject) message.body();
        String username = body.getString(USERNAME);
        body.remove(USERNAME);
        Vehicle responseVehicle = new JsonObject(body.getString(VEHICLE)).mapTo(Vehicle.class);
        JsonObject responseJson = new JsonObject();
        dataToUser.put(BODY, responseJson.mapFrom(responseVehicle).encode());
        publishToUser(username, dataToUser);
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

}
