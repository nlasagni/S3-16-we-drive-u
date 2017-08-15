package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.EventBus.BODY;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.REGISTER_RESPONSE;
import static com.wedriveu.shared.util.Constants.Vehicle.LICENCE_PLATE;


/**
 * Vert.x RabbitMQ Publisher for vehicle register response. Replies the vehicle with the adding result to the database.
 *
 * @author Marco Baldassarri on 08/08/2017.
 */
public class RegisterPublisherVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.VehicleStore.REGISTER_VEHICLE_COMPLETED, this::sendRegisterResponse);
        super.start(startFuture);
    }

    private void sendRegisterResponse(Message message) {
        JsonObject json = (JsonObject) message.body();
        String licencePlate = json.getString(LICENCE_PLATE);
        json.remove(LICENCE_PLATE);
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(REGISTER_RESPONSE, licencePlate),
                new JsonObject().put(BODY, json.toString()), onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(RegisterPublisherVerticle.class.getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }

}
