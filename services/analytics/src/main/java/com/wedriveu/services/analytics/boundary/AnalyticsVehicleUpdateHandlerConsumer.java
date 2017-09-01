package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.util.ConstantsAnalytics;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * a verticle for publishing all the updates to the backoffice
 *
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleUpdateHandlerConsumer extends VerticleConsumer {
    public AnalyticsVehicleUpdateHandlerConsumer() {
        super(String.format(com.wedriveu.shared.util.Constants.FORMAT_WITH_DOT,
                com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.ANALYTICS,
                com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE));
    }

    @Override
    public void start(Future futureRequest) throws Exception {
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v -> {
            if (v.succeeded()) {
                futureRequest.complete();
            } else {
                futureRequest.fail(v.cause());
            }
        });
        startConsumerWithFuture(com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.VEHICLE,
                com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE,
                ConstantsAnalytics.EventBus.AVAILABLE_ADDRESS_VEHICLE_UPDATE_HANDLER,
                futureConsumer);

    }


    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendToController);
    }

    private void sendToController(Message message) {
        VehicleUpdate vehicle = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleUpdate.class);
        vertx.eventBus().send(ConstantsAnalytics.EventBus.VEHICLE_COUNTER_UPDATE, VertxJsonMapper.mapInBodyFrom(vehicle));
    }

}
