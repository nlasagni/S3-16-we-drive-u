package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * This Verticle uses RabbitMQ Vertx.x library to publish the chosen vehicle to the client.
 *
 * @author Marco Baldassarri
 * @since 4/08/2017
 */
public class VehicleElectionVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start(future);
        vertx.eventBus().consumer(Messages.VehicleStore.GET_VEHICLE_COMPLETED, this::sendVehicleToUser);
    }

    private void sendVehicleToUser(Message message) {
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(BODY, ((JsonObject) message.body()).encode());
        publish(VEHICLE_SERVICE_EXCHANGE,
                String.format(ROUTING_KEY_VEHICLE_RESPONSE, dataToUser.getString(USERNAME)),
                dataToUser);
    }

}
