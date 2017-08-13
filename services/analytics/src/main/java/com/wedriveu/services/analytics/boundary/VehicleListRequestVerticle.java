package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.shared.util.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListRequestVerticle extends VerticlePublisher {

    @Override
    public void start() throws Exception {
        startConsumer();
        //Log.log("future VehicleListRequestVerticle complete");
    }

    private void startConsumer() {
        //Log.log("started vertx eventbus consumer in VehicleListRequestVerticle, attending start to receive");
        vertx.eventBus().consumer(ANALYTICS_VEHICLE_LIST_REQUEST_VERTICLE_ADDRESS, this::requestVehicleListToVehicleService);
    }

    private void requestVehicleListToVehicleService(Message message) {
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(EventBus.BODY, VEHICLE_REQUEST_ALL_MESSAGE);
        publish(RabbitMQ.Exchanges.ANALYTICS,ROUTING_KEY_VEHICLE_REQUEST_ALL,dataToUser);
        //Log.log("sent request for all vehicles to vehicle service in VehicleListRequestVerticle");
    }
}
