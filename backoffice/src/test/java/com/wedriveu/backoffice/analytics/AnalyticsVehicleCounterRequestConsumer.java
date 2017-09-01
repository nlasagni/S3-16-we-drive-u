package com.wedriveu.backoffice.analytics;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleCounterRequestConsumer extends VerticleConsumer {

    public AnalyticsVehicleCounterRequestConsumer() {
        super(ConstantsBackOffice.Queues.ANALYTYCS_VEHICLE_COUNTER_REQUEST_QUEUE_TEST);
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

        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.ANALYTICS,
                Constants.RabbitMQ.RoutingKey.ANALYTICS_REQUEST_VEHICLE_COUNTER,
                ConstantsBackOffice.EventBus.ANALYTICS_VEHICLE_COUNTER_REQUEST_EVENTBUS_AVAILABLE_TEST,
                futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendUpdatesToController);
    }

    private void sendUpdatesToController(Message message) {
        JsonObject dataToUser = new JsonObject(message.body().toString());
        vertx.eventBus().send(ConstantsBackOffice.EventBus.BACKOFFICE_VEHICLE_COUNTER_REQUEST_TEST, dataToUser);
    }

}
