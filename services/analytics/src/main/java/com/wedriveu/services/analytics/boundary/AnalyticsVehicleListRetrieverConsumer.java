package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleListRetrieverConsumer extends VerticleConsumer {
    public AnalyticsVehicleListRetrieverConsumer() {
        super(RabbitMQ.Exchanges.ANALYTICS + "." + ROUTING_KEY_VEHICLE_RESPONSE_ALL);
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
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.VEHICLE, ROUTING_KEY_VEHICLE_RESPONSE_ALL, ANALYTICS_EVENTBUS_AVAILABLE_ADDRESS_VEHICLE_LIST_RETRIEVER_VERTICLE, futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendToController);
    }

    private void sendToController(Message message) {
        AnalyticsVehicleList vehicleListObject =
                VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), AnalyticsVehicleList.class);
        vertx.eventBus().send(ANALYTICS_CONTROLLER_VEHICLE_LIST_EVENTBUS, VertxJsonMapper.mapInBodyFrom(vehicleListObject));
    }

}
