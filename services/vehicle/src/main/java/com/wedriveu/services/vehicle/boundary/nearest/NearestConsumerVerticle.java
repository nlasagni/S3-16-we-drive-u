package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.shared.util.Constants.EVENT_BUS_AVAILABLE_ADDRESS;


/**
 * Starts the consumer which handles the user request for the vehicle.
 * The information the consumer expects to receive are the user current position (Latitude, Longitude), the chosen
 * destination position (Latitude, Longitude) and the username associated with the user.
 *
 * @author Marco Baldassarri
 * @since 30/07/2017
 */
public class NearestConsumerVerticle extends VerticleConsumer {

    public NearestConsumerVerticle() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);
    }

    @Override
    public void start() throws Exception {
        super.start();
        startUserConsumer();
    }

    private void startUserConsumer() throws IOException, TimeoutException {
        startConsumer(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST,
                EVENT_BUS_AVAILABLE_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            JsonObject message = (JsonObject) msg.body();
            JsonObject response = new JsonObject(message.getString(Constants.EventBus.BODY));
            searchAvailableVehicles(response);
        });
    }

    private void searchAvailableVehicles(JsonObject userData) {
        eventBus.send(Messages.NearestControl.AVAILABLE_REQUEST, userData);
    }

}
