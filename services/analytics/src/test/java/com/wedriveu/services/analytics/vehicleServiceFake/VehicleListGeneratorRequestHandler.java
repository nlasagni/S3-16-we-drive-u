package com.wedriveu.services.analytics.vehicleServiceFake;


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
public class VehicleListGeneratorRequestHandler extends VerticleConsumer{

    public VehicleListGeneratorRequestHandler() {
        super(RabbitMQ.Exchanges.ANALYTICS +"."+ ROUTING_KEY_VEHICLE_REQUEST_ALL);
    }

    @Override
    public void start(Future futureRequest) throws Exception {
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
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.VEHICLE, ROUTING_KEY_VEHICLE_REQUEST_ALL, ANALYTICS_EVENTBUS_AVAILABLE_ADDRESS_FAKE_GENERATOR, futureConsumer);

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
