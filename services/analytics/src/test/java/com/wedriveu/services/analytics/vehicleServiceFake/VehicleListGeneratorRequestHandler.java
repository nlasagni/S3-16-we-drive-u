package com.wedriveu.services.analytics.vehicleServiceFake;


import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.utils.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListGeneratorRequestHandler extends VerticleConsumer{

    public VehicleListGeneratorRequestHandler() {
        super("VehicleListGeneratorRequestHandler");
    }

    @Override
    public void start(Future futureRequest) throws Exception {
        //setQueueName(RabbitMQ.Exchanges.ANALYTICS +"."+ ROUTING_KEY_VEHICLE_REQUEST_ALL);
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v->{
            if (v.succeeded()) {
                Log.log("future in VehicleListGeneratorRequestHandler completed");
                futureRequest.complete();
            } else {
                Log.error("future VehicleListGeneratorRequestHandler fail", v.cause().getLocalizedMessage(), v.cause());
                futureRequest.fail(v.cause());
            }
        });
        //startConsumerWithFuture(RabbitMQ.Exchanges.ANALYTICS, ROUTING_KEY_VEHICLE_REQUEST_ALL, EVENT_BUS_AVAILABLE_ADDRESS, futureConsumer);

    }

    @Override
    public void registerConsumer(String eventBus) {
        Log.log("started vertx eventbus consumer in VehicleListGeneratorRequestHandler, attending start to receive");
        vertx.eventBus().consumer(eventBus, this::sendToResponseHandler);
    }

    private void sendToResponseHandler(Message message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(EventBus.BODY,
                ((JsonObject) message.body()).getValue(EventBus.BODY));
        vertx.eventBus().send(ANALYTICS_TEST_VEHICLE_LIST_REQUEST_EVENTBUS, jsonObject);
    }
}
