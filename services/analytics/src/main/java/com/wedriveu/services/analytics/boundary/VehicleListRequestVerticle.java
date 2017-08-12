package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListRequestVerticle extends VerticlePublisher {

    @Override
    public void start() throws Exception {
        startConsumer();
        Log.log("future VehicleListRequestVerticle complete");
    }

    private void startConsumer() {
        Log.log("started vertx eventbus consumer in VehicleListRequestVerticle, attending start to receive");
        vertx.eventBus().consumer(ANALYTICS_VEHICLE_LIST_REQUEST_VERTICLE_ADDRESS, this::requestVehicleListToVehicleService);
    }


    private void requestVehicleListToVehicleService(Message message) {
        //if (message.body().toString()!= )
        System.out.println("VehicleListRequestVerticle " + message.body().toString());
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(BODY, VEHICLE_REQUEST_ALL_MESSAGE);
        publish(ANALYTICS_SERVICE_EXCHANGE,ROUTING_KEY_VEHICLE_REQUEST_ALL,dataToUser);
        Log.log("sent request for all vehicles to vehicle service in VehicleListRequestVerticle");
    }

    /*
    private void startVehicleListRetriever(AsyncResult<Void> voidAsyncResult) {
        vertx.eventBus().send(ANALYTICS_VEHICLE_LIST_RETRIEVER_VERTICLE_ADDRESS, ANALYTICS_VEHICLE_LIST_RETRIEVER_START_MESSAGE);
        Log.log("sent message to " + ANALYTICS_VEHICLE_LIST_RETRIEVER_VERTICLE_ADDRESS + "for start retrieving vehicle list");
    }
    */
}
