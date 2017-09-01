package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.NEAREST_EVENT_BUS_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_NEAREST;


/**
 * Starts the consumer which handles the user request for the vehicle.
 * The information the consumer expects to receive are the user current position (Latitude, Longitude), the chosen
 * destination position (Latitude, Longitude) and the username associated with the user.
 *
 * @author Marco Baldassarri on 30/07/2017.
 */
public class NearestConsumerVerticle extends VerticleConsumer {

    public NearestConsumerVerticle() {
        super(VEHICLE_SERVICE_QUEUE_NEAREST);
    }

    @Override
    public void start(Future startFuture) throws Exception {
        super.start();
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST,
                NEAREST_EVENT_BUS_ADDRESS,
                startFuture);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            JsonObject message = (JsonObject) msg.body();
            JsonObject nearestRequest = new JsonObject(message.getString(Constants.EventBus.BODY));
            searchAvailableVehicles(nearestRequest);
        });
    }

    private void searchAvailableVehicles(JsonObject userData) {
        eventBus.send(Messages.NearestControl.AVAILABLE_REQUEST, userData);
    }

}
