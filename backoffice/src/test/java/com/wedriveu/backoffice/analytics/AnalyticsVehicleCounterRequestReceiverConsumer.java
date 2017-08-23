package com.wedriveu.backoffice.analytics;

import com.wedriveu.backoffice.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.BookingListRequest;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleCounterRequestReceiverConsumer extends VerticleConsumer {

    public AnalyticsVehicleCounterRequestReceiverConsumer() {
        super(EventBus.ANALYTYCS_VEHICLE_COUNTER_REQUEST_QUEUE_TEST);
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
                Constants.RabbitMQ.RoutingKey.ROUTING_KEY_ANALYTICS_REQUEST_VEHICLE_LIST,
                EventBus.ANALYTICS_VEHICLE_COUNTER_REQUEST_EVENTBUS_AVAILABLE_TEST,
                futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendUpdatesToController);
    }

    private void sendUpdatesToController(Message message) {
        JsonObject dataToUser = new JsonObject(message.body().toString());
        String backofficeId = dataToUser.getValue(Constants.EventBus.BODY).toString();
        vertx.eventBus().send(EventBus.BACKOFFICE_VEHICLE_COUNTER_REQUEST_TEST, dataToUser);
    }

}
