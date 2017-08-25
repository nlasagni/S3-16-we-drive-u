package com.wedriveu.services.analytics.vehicleService;


import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListGeneratorRequestHandler extends VerticleConsumer {

    public VehicleListGeneratorRequestHandler() {
        super(RabbitMQ.Exchanges.ANALYTICS + "." + RabbitMQ.RoutingKey.ANALYTICS_VEHICLE_REQUEST_ALL + ".test");
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
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.VEHICLE, RabbitMQ.RoutingKey.ANALYTICS_VEHICLE_REQUEST_ALL, EventBus.AVAILABLE_ADDRESS_FAKE_GENERATOR, futureConsumer);

    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendToResponseHandler);
    }

    private void sendToResponseHandler(Message message) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY,
                ((JsonObject) message.body()).getValue(Constants.EventBus.BODY));
        vertx.eventBus().send(EventBus.TEST_VEHICLE_LIST_REQUEST, jsonObject);
    }
}
