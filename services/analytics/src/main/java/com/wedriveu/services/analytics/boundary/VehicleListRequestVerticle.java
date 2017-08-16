package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import static com.wedriveu.shared.util.Constants.*;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLE_REQUEST_ALL;

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
        vertx.eventBus().consumer(ANALYTICS_VEHICLE_LIST_REQUEST_EVENTBUS, this::requestVehicleListToVehicleService);
    }

    private void requestVehicleListToVehicleService(Message message) {
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(EventBus.BODY, VEHICLE_REQUEST_ALL_MESSAGE);
        publish(RabbitMQ.Exchanges.ANALYTICS,ANALYTICS_VEHICLE_REQUEST_ALL,dataToUser, published -> { });
        //Log.log("sent request for all vehicles to vehicle service in VehicleListRequestVerticle");
    }
}
