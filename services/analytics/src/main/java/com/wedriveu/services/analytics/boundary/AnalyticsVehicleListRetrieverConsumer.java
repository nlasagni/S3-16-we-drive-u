package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.util.ConstantsAnalytics;
import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ;

/**
 * a verticle for receiving the vehicle list from the Vehicle service
 *
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleListRetrieverConsumer extends VerticleConsumer {
    public AnalyticsVehicleListRetrieverConsumer() {
        super(String.format(com.wedriveu.shared.util.Constants.FORMAT_WITH_DOT,
                RabbitMQ.Exchanges.ANALYTICS,
                RabbitMQ.RoutingKey.ANALYTICS_VEHICLES_RESPONSE_ALL));
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
        startConsumerWithFuture(com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.VEHICLE,
                RabbitMQ.RoutingKey.ANALYTICS_VEHICLES_RESPONSE_ALL,
                ConstantsAnalytics.EventBus.AVAILABLE_ADDRESS_VEHICLE_LIST_RETRIEVER_VERTICLE,
                futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendToController);
    }

    private void sendToController(Message message) {
        AnalyticsVehicleList vehicleListObject =
                VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), AnalyticsVehicleList.class);
        vertx.eventBus().send(ConstantsAnalytics.EventBus.CONTROLLER_VEHICLE_LIST, VertxJsonMapper.mapInBodyFrom(vehicleListObject));
    }

}
