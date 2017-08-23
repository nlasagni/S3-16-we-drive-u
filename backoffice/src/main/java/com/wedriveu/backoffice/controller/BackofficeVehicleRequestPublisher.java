package com.wedriveu.backoffice.controller;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;


/**
 * the class for sending a request of the Vehicle list over rabbitMQ with vertx
 * @author Stefano Bernagozzi
 */
public class BackofficeVehicleRequestPublisher extends VerticlePublisher {
    String backofficeId;

    public BackofficeVehicleRequestPublisher(String backofficeId) {
        this.backofficeId = backofficeId;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future future = Future.future();
        super.start(future);
        future.setHandler(res -> {
            startFuture.complete();
            requestVehicleCounterToAnalytics();
        });
    }

    private void requestVehicleCounterToAnalytics() {
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(Constants.EventBus.BODY, backofficeId);
        publish(Constants.RabbitMQ.Exchanges.ANALYTICS,
                Constants.RabbitMQ.RoutingKey.ROUTING_KEY_ANALYTICS_REQUEST_VEHICLE_LIST,
                dataToUser,
                published -> { });
    }
}
