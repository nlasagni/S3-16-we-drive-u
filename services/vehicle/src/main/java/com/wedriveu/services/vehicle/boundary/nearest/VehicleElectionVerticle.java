package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.USERNAME;

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
        future.complete();
    }

    private void sendVehicleToUser(Message message) {
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(Constants.EventBus.BODY, ((JsonObject) message.body()).encode());
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE, dataToUser.getString(USERNAME)),
                dataToUser);
    }

}
