package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLES;
import static com.wedriveu.shared.util.Constants.RabbitMQ;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehiclesResponseConsumer extends VerticleConsumer {
    String backofficeId;

    public AnalyticsVehiclesResponseConsumer(String queueName, String backofficeId) {
        super(Constants.RabbitMQ.Exchanges.ANALYTICS + "." + ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLES + queueName);
        this.backofficeId = backofficeId;
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v -> {
            if (v.succeeded()) {
                futureRetriever.complete();
                Log.info("started future retriever AnalyticsVehiclesResponseConsumer");
            } else {
                Log.error("AnalyticsVehiclesResponseConsumer", v.cause().getLocalizedMessage(), v.cause());
                futureRetriever.fail(v.cause());
            }
        });
        String eventBusAvailable = EventBus.AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_NO_ID;
        if (!backofficeId.equals("")) {
            eventBusAvailable = EventBus.AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_WITH_ID;
        }

        startConsumerWithFuture(RabbitMQ.Exchanges.ANALYTICS,
                ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLES + backofficeId,
                eventBusAvailable,
                futureConsumer);
        Log.info("started consumer " + eventBusAvailable);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendUpdatesToController);
    }

    private void sendUpdatesToController(Message message) {
        VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleCounter.class);
        vertx.eventBus().send(EventBus.BACKOFFICE_CONTROLLER, VertxJsonMapper.mapInBodyFrom(vehicleCounter));
        Log.info("AnalyticsVehiclesResponseConsumer received updates and send to controller");
    }
}
