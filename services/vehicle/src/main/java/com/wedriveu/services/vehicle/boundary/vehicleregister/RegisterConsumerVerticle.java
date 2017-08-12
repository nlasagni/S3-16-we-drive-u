package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.utilities.MessageParser;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * Vert.x RabbitMQ Consumer that listens for new vehicle register requests.
 *
 * @author Marco Baldassarri
 * @since 08/08/2017
 */
public class RegisterConsumerVerticle extends VerticleConsumer {

    private static final String REGISTER_EVENT_BUS_ADDRESS = RegisterConsumerVerticle.class.getCanonicalName();

    public RegisterConsumerVerticle() {
        super(VEHICLE_SERVICE_QUEUE_REGISTER);
    }

    @Override
    public void start() throws Exception {
        super.start();
        startVehicleRegisterConsumer();
    }

    private void startVehicleRegisterConsumer() throws IOException, TimeoutException {
        startConsumer(VEHICLE_SERVICE_EXCHANGE, ROUTING_KEY_REGISTER_VEHICLE_REQUEST, REGISTER_EVENT_BUS_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            addNewVehicle(MessageParser.getJson(msg));
        });
    }

    private void addNewVehicle(JsonObject userData) {
        eventBus.send(Messages.VehicleRegister.REGISTER_VEHICLE_REQUEST, userData);
    }

}
