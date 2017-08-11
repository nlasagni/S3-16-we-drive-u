package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;

import java.util.concurrent.Callable;

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListRetrieverVerticle extends VerticleConsumer{
    public VehicleListRetrieverVerticle() {
        super(CONSUMER_ANALYTICS_SERVICE);
    }
    Future futureLocal;

    @Override
    public void start(Future futureRetriever) throws Exception {
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v->{
            if (v.succeeded()) {
                Log.log("future in VehicleListRetrieverVerticle completed");
                futureRetriever.complete();
            } else {
                Log.error("future consumer handler", v.cause().getLocalizedMessage(), v.cause());
                futureRetriever.fail(v.cause());
            }
        });
        startConsumerWithFuture(VEHICLE_SERVICE_EXCHANGE, ROUTING_KEY_VEHICLE_RESPONSE_ALL, EVENT_BUS_AVAILABLE_ADDRESS, futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        Log.log("started vertx eventbus consumer in VehicleListRetrieverVerticle, attending start to receive");
        vertx.eventBus().consumer(eventBus, this::sendToController);
    }

    private void sendToController(Message message) {
        vertx.eventBus().send(ANALYTICS_CONTROLLER_VEHICLE_LIST_VERTICLE_ADDRESS, message);
        Log.log("sent vehicle list to analytics controller");
    }

}
