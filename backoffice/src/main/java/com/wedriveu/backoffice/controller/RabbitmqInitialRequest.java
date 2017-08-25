package com.wedriveu.backoffice.controller;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ROUTING_KEY_ANALYTICS_REQUEST_VEHICLE_LIST;

/**
 * @author Stefano Bernagozzi
 */
public class RabbitmqInitialRequest extends VerticlePublisher {
    String backofficeId;

    public RabbitmqInitialRequest(String backofficeId) {
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
        publish(Constants.RabbitMQ.Exchanges.ANALYTICS, ROUTING_KEY_ANALYTICS_REQUEST_VEHICLE_LIST, dataToUser, published -> {
        });
    }
}
