package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Starts the consumer which handles the user request for the nearest vehicle.
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

    private void startUserConsumer() {
        try {
            startConsumer(onStart -> {
                declareQueue(onQueue -> {
                    if (onQueue.succeeded()) {
                        bindQueueToExchange(Constants.VEHICLE_SERVICE_EXCHANGE,
                                Constants.ROUTING_KEY_VEHICLE_REQUEST, onBind -> {
                                    if (onBind.succeeded()) {
                                        registerConsumer(Constants.EVENT_BUS_AVAILABLE_ADDRESS);
                                        basicConsume(Constants.EVENT_BUS_AVAILABLE_ADDRESS);
                                    }
                                });
                    }
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> searchAvailableVehicles((JsonObject) msg.body()));
    }

    private void searchAvailableVehicles(JsonObject userData) {
        eventBus.send(Messages.NearestControl.AVAILABLE_REQUEST, userData);
    }

}
