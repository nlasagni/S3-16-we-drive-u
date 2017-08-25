package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleRequestConsumer extends VerticleConsumer {
    public AnalyticsVehicleRequestConsumer() {
        super(Constants.RabbitMQ.Exchanges.ANALYTICS + "." + Constants.RabbitMQ.RoutingKey.ANALYTICS_REQUEST_VEHICLE_LIST);
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v -> {
            if (v.succeeded()) {
                futureRetriever.complete();
            } else {
                futureRetriever.fail(v.cause());
            }
        });
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.ANALYTICS, Constants.RabbitMQ.RoutingKey.ANALYTICS_REQUEST_VEHICLE_LIST, EventBus.AVAILABLE_ADDRESS_COUNTER_REQUEST, futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendToController);
    }

    private void sendToController(Message message) {
        JsonObject dataToUser = new JsonObject(message.body().toString());
        String backofficeId = dataToUser.getValue(Constants.EventBus.BODY).toString();
        dataToUser.put(Constants.EventBus.BODY, backofficeId);
        vertx.eventBus().send(EventBus.VEHICLE_COUNTER_REQUEST, dataToUser);
    }

}
