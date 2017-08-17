package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleCounterRequestHandlerVerticle extends VerticleConsumer{
    public VehicleCounterRequestHandlerVerticle() {
        super(Constants.RabbitMQ.Exchanges.ANALYTICS +"."+ ROUTING_KEY_ANALYTICS_REQUEST_VEHICLES);
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v->{
            if (v.succeeded()) {
                Log.log("future in VehicleCounterRequestHandlerVerticle completed");
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
        Log.log("started vertx eventbus consumer in VehicleCounterRequestHandlerVerticle, attending start to receive");
        vertx.eventBus().consumer(eventBus, this::sendToController);
    }

    private void sendToController(Message message) {
        String backofficeID = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), String.class);
        vertx.eventBus().send(ANALYTICS_VEHICLE_COUNTER_REQUEST_EVENTBUS, VertxJsonMapper.mapInBodyFrom(backofficeID));
    }

}