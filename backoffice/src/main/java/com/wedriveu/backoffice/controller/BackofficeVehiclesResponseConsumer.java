package com.wedriveu.backoffice.controller;

import com.wedriveu.backoffice.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLE_LIST;
import static com.wedriveu.shared.util.Constants.RabbitMQ;

/**
 * the class for recieving a vehicle list over rabbitMQ with vertx from the vehicle service
 *
 * @author Stefano Bernagozzi
 */
public class BackofficeVehiclesResponseConsumer extends VerticleConsumer {
    private String backofficeId;

    /**
     * @param queueName the rabbitMQ queue name
     * @param backofficeId null in case you want to listen over the backoffice update queue,
     *                     "." + backoffice id in case you want to listen in your own routing key (only for the initial update)
     */
    public BackofficeVehiclesResponseConsumer(String queueName, String backofficeId) {
        super(Constants.RabbitMQ.Exchanges.ANALYTICS + "." + ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLE_LIST + queueName);
        this.backofficeId = backofficeId;
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
        String eventBusAvailable = EventBus.AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_NO_ID;
        if (!backofficeId.equals("")) {
            eventBusAvailable = EventBus.AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_WITH_ID;
        }

        startConsumerWithFuture(RabbitMQ.Exchanges.ANALYTICS,
                ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLE_LIST + backofficeId,
                eventBusAvailable,
                futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendUpdatesToController);
    }

    private void sendUpdatesToController(Message message) {
        VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleCounter.class);
        vertx.eventBus().send(EventBus.BACKOFFICE_CONTROLLER_VEHICLES, VertxJsonMapper.mapInBodyFrom(vehicleCounter));
    }
}
