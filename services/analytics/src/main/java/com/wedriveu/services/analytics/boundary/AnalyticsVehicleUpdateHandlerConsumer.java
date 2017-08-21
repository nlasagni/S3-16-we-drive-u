package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleUpdateHandlerConsumer extends VerticleConsumer {
    public AnalyticsVehicleUpdateHandlerConsumer() {
        super(Constants.RabbitMQ.Exchanges.ANALYTICS + "." + Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE);
    }

    @Override
    public void start(Future futureRequest) throws Exception {
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v -> {
            if (v.succeeded()) {
                Log.info("future in VehicleListGeneratorRequestHandler completed");
                futureRequest.complete();
            } else {
                Log.error("future VehicleListGeneratorRequestHandler fail", v.cause().getLocalizedMessage(), v.cause());
                futureRequest.fail(v.cause());
            }
        });
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.VEHICLE, Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE, EventBus.AVAILABLE_ADDRESS_VEHICLE_UPDATE_HANDLER, futureConsumer);

    }


    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendToController);
    }

    private void sendToController(Message message) {
        Log.info("received vehicle list");
        UpdateToService vehicle = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), UpdateToService.class);
        vertx.eventBus().send(EventBus.VEHICLE_COUNTER_UPDATE, VertxJsonMapper.mapInBodyFrom(vehicle));
    }


}
