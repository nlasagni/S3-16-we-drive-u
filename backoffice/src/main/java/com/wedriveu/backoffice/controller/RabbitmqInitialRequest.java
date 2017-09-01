package com.wedriveu.backoffice.controller;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

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
        publish(Constants.RabbitMQ.Exchanges.ANALYTICS,
                Constants.RabbitMQ.RoutingKey.ANALYTICS_REQUEST_VEHICLE_COUNTER,
                dataToUser,
                published -> { });
    }
}
