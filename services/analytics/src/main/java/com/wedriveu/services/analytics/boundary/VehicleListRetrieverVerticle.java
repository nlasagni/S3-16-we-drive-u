package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.shared.entity.VehicleListObject;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListRetrieverVerticle extends VerticleConsumer{
    public VehicleListRetrieverVerticle() {
        super("VehicleListRetrieverVerticle");
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        setQueueName(RabbitMQ.Exchanges.ANALYTICS +"."+ ROUTING_KEY_VEHICLE_RESPONSE_ALL);
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
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.ANALYTICS, ROUTING_KEY_VEHICLE_RESPONSE_ALL, EVENT_BUS_AVAILABLE_ADDRESS, futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        Log.log("started vertx eventbus consumer in VehicleListRetrieverVerticle, attending start to receive");
        vertx.eventBus().consumer(eventBus, this::sendToController);
    }

    private void sendToController(Message message) {
        System.out.println("VehicleListRetrieverVerticle " + message.body().toString());
        VehicleListObject vehicleListObject = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleListObject.class);
        vertx.eventBus().send(ANALYTICS_CONTROLLER_VEHICLE_LIST_VERTICLE_ADDRESS, VertxJsonMapper.mapInBodyFrom(vehicleListObject));
        Log.log("sent vehicle list to analytics controller");
    }

}
