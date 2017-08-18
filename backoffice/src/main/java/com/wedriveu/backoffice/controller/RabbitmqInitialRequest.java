package com.wedriveu.backoffice.controller;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.ROUTING_KEY_ANALYTICS_REQUEST_VEHICLES;

/**
 * @author Stefano Bernagozzi
 */
public class RabbitmqInitialRequest extends VerticlePublisher{
    String backofficeId;

    public RabbitmqInitialRequest(String backofficeId) {
        this.backofficeId = backofficeId;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future future = Future.future();
        super.start(future);
        future.setHandler(res-> {
            startFuture.complete();
            requestVehicleCounterToAnalytics();
        });
    }

    private void requestVehicleCounterToAnalytics() {
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(Constants.EventBus.BODY, backofficeId);
        Log.log(Constants.RabbitMQ.Exchanges.ANALYTICS + ROUTING_KEY_ANALYTICS_REQUEST_VEHICLES);
        publish(Constants.RabbitMQ.Exchanges.ANALYTICS, ROUTING_KEY_ANALYTICS_REQUEST_VEHICLES,dataToUser, published -> { });
        Log.log("sending initial request");
    }
}
