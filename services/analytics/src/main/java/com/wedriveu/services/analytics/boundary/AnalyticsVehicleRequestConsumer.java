package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.util.ConstantsAnalytics;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * a verticle for receiving the vehicle counter request
 *
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleRequestConsumer extends VerticleConsumer {
    public AnalyticsVehicleRequestConsumer() {
        super(String.format(com.wedriveu.shared.util.Constants.FORMAT_WITH_DOT,
                com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.ANALYTICS,
                com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_REQUEST_VEHICLE_COUNTER));
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
        startConsumerWithFuture(com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.ANALYTICS,
                com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_REQUEST_VEHICLE_COUNTER,
                ConstantsAnalytics.EventBus.AVAILABLE_ADDRESS_COUNTER_REQUEST,
                futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendToController);
    }

    private void sendToController(Message message) {
        JsonObject dataToUser = new JsonObject(message.body().toString());
        String backofficeId = dataToUser.getValue(com.wedriveu.shared.util.Constants.EventBus.BODY).toString();
        dataToUser.put(com.wedriveu.shared.util.Constants.EventBus.BODY, backofficeId);
        vertx.eventBus().send(ConstantsAnalytics.EventBus.VEHICLE_COUNTER_REQUEST, dataToUser);
    }

}
