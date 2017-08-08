package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * Created by Marco on 08/08/2017.
 */
public class RegisterConsumerVerticle extends VerticleConsumer {

    public RegisterConsumerVerticle() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);
    }

    @Override
    public void start() throws Exception {
        super.start();
        startVehicleRegisterConsumer();
    }

    private void startVehicleRegisterConsumer() throws IOException, TimeoutException {
        startConsumer(VEHICLE_SERVICE_EXCHANGE, ROUTING_KEY_REGISTER_VEHICLE_REQUEST, EVENT_BUS_AVAILABLE_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> addNewVehicle((JsonObject) msg.body()));
    }

    private void addNewVehicle(JsonObject userData) {
        eventBus.send(Messages.VehicleRegister.REGISTER_VEHICLE_REQUEST, userData);
    }
}
