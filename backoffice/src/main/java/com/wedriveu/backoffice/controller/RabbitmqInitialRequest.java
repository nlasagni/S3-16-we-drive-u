package com.wedriveu.backoffice.controller;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.shared.util.Constants;
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
    public void start() throws Exception {
        requestVehicleCounterToAnalytics();
    }

    private void requestVehicleCounterToAnalytics() {
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(Constants.EventBus.BODY, backofficeId);
        publish(Constants.RabbitMQ.Exchanges.ANALYTICS, ROUTING_KEY_ANALYTICS_REQUEST_VEHICLES,dataToUser, published -> { });
    }
}
