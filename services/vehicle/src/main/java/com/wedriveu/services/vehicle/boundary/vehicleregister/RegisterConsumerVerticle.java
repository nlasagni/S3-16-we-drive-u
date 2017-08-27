package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.EVENT_BUS_REGISTER_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_REGISTER;
import static com.wedriveu.shared.util.Constants.EventBus.BODY;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.REGISTER_REQUEST;


/**
 * Vert.x RabbitMQ Consumer that listens for new vehicle register requests.
 *
 * @author Marco Baldassarri on 08/08/2017.
 */
public class RegisterConsumerVerticle extends VerticleConsumer {

    public RegisterConsumerVerticle() {
        super(VEHICLE_SERVICE_QUEUE_REGISTER);
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();
        startVehicleRegisterConsumer(startFuture);
    }

    private void startVehicleRegisterConsumer(Future future) throws IOException, TimeoutException {
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.VEHICLE,
                REGISTER_REQUEST,
                EVENT_BUS_REGISTER_ADDRESS,
                future);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            JsonObject body = (JsonObject) msg.body();
            addNewVehicle(new JsonObject(body.getString(BODY)));
        });
    }

    private void addNewVehicle(JsonObject userData) {
        eventBus.send(Messages.VehicleRegister.REGISTER_VEHICLE_REQUEST, userData);
    }

}
