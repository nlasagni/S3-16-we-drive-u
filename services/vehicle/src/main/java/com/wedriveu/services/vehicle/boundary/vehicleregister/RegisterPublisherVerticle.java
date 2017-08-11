package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * Created by Marco on 08/08/2017.
 */
public class RegisterPublisherVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.VehicleStore.REGISTER_VEHICLE_COMPLETED, this::sendRegisterResponse);
        super.start(startFuture);
    }

    private void sendRegisterResponse(Message message) {
        JsonObject json = (JsonObject) message.body();
        String licencePlate = json.getString(CAR_LICENCE_PLATE);
        json.remove(CAR_LICENCE_PLATE);
        Log.info("======", "REGISTER PUBLISHER VERTICLE -  SEND REGISTER RESPONSE");
        Log.info("SERVICE", "\nEXCHANGE_NAME:" + VEHICLE_SERVICE_EXCHANGE +
                "\nROUTING_KEY: " + String.format(ROUTING_KEY_REGISTER_VEHICLE_RESPONSE, licencePlate) +
                "\nDATA: " + json.encodePrettily());
        publish(VEHICLE_SERVICE_EXCHANGE,
                String.format(ROUTING_KEY_REGISTER_VEHICLE_RESPONSE, licencePlate),
                json);
    }
}
