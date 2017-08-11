package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.shared.utilities.MessageParser;
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
        Log.info("REGISTER CONSUMER VERTICLE", "START SETUP");
        super.start();
        startVehicleRegisterConsumer();
        Log.info("REGISTER CONSUMER VERTICLE", "END SETUP");
    }

    private void startVehicleRegisterConsumer() throws IOException, TimeoutException {
        startConsumer(VEHICLE_SERVICE_EXCHANGE, ROUTING_KEY_REGISTER_VEHICLE_REQUEST, EVENT_BUS_AVAILABLE_ADDRESS);
        Log.info("CONSUMER", "STARTED");
    }

    @Override
    public void registerConsumer(String eventBus) {
        Log.info("====== REGISTER CONSUMER", "ADD NEW VEHICLE REGISTER CONSUMER");

        vertx.eventBus().consumer(eventBus, msg -> {
            Log.info("====== REGISTER CONSUMER", "ADD NEW VEHICLE EVENT BUS HANDLER");
            addNewVehicle(MessageParser.getJson(msg));
        });
    }

    private void addNewVehicle(JsonObject userData) {
        Log.info("====== REGISTER CONSUMER", "ADD NEW VEHICLE ADD");

        eventBus.send(Messages.VehicleRegister.REGISTER_VEHICLE_REQUEST, userData);
    }

}
