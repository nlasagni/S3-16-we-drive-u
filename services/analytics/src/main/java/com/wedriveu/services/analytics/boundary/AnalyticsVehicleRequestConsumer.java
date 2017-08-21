package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleRequestConsumer extends VerticleConsumer {
    public AnalyticsVehicleRequestConsumer() {
        super(Constants.RabbitMQ.Exchanges.ANALYTICS + "." + ROUTING_KEY_ANALYTICS_REQUEST_VEHICLES);
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v -> {
            if (v.succeeded()) {
                Log.info("future in AnalyticsVehicleRequestConsumer completed");
                futureRetriever.complete();
            } else {
                Log.error("future consumer handler", v.cause().getLocalizedMessage(), v.cause());
                futureRetriever.fail(v.cause());
            }
        });
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.ANALYTICS, ROUTING_KEY_ANALYTICS_REQUEST_VEHICLES, ANALYTICS_EVENTBUS_AVAILABLE_ADDRESS_COUNTER_REQUEST, futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        Log.info("started vertx eventbus consumer in AnalyticsVehicleRequestConsumer, attending start to receive");
        vertx.eventBus().consumer(eventBus, this::sendToController);
    }

    private void sendToController(Message message) {
        JsonObject dataToUser = new JsonObject(message.body().toString());
        String backofficeId = dataToUser.getValue(EventBus.BODY).toString();
        dataToUser.put(Constants.EventBus.BODY, backofficeId);
        vertx.eventBus().send(ANALYTICS_VEHICLE_COUNTER_REQUEST_EVENTBUS, dataToUser);
    }

}
