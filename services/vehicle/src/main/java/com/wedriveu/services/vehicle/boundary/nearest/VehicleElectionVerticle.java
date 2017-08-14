package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.util.Log;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.EventBus.BODY;
import static com.wedriveu.shared.util.Constants.USERNAME;

/**
 * This Verticle uses RabbitMQ Vertx.x library to publish the chosen vehicle to the client.
 *
 * @author Marco Baldassarri on 4/08/2017.
 */
public class VehicleElectionVerticle extends VerticlePublisher {

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(Messages.VehicleStore.GET_VEHICLE_COMPLETED, this::sendVehicleToUser);
    }

    private void sendVehicleToUser(Message message) {
        JsonObject dataToUser = new JsonObject();
        Vehicle responseVehicle = (Vehicle) message.body();
        JsonObject responseJson = new JsonObject();
        dataToUser.put(BODY, responseJson.mapFrom(responseVehicle).encode());
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE, dataToUser.getString(USERNAME)),
                dataToUser, onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(VehicleElectionVerticle.class.getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }

}
