package com.wedriveu.backoffice.controller;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class RabbitmqListenerUpdate extends VerticleConsumer{
    String backofficeId;

    public RabbitmqListenerUpdate(String queueName, String backofficeId ) {
        super(Constants.RabbitMQ.Exchanges.ANALYTICS +"."+ ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLES +  queueName );
        this.backofficeId = backofficeId;
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v->{
            if (v.succeeded()) {
                futureRetriever.complete();
                Log.log("started future retriever RabbitmqListenerUpdate");
            } else {
                Log.error("RabbitmqListenerUpdate", v.cause().getLocalizedMessage(), v.cause());
                futureRetriever.fail(v.cause());
            }
        });
        String eventBusAvailable = BACKOFFICE_EVENTBUS_AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_NO_ID;
        if (!backofficeId.equals("")) {
            eventBusAvailable = BACKOFFICE_EVENTBUS_AVAILABLE_ADDRESS_RABBITMQ_LISTENER_UPDATE_WITH_ID;
        }

        startConsumerWithFuture(RabbitMQ.Exchanges.ANALYTICS,
                    ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLES + backofficeId,
                    eventBusAvailable,
                    futureConsumer);
        Log.log("started consumer " + eventBusAvailable);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendUpdatesToController);
    }

    private void sendUpdatesToController(Message message) {
        Log.log("RabbitmqListenerUpdate received updates and send to controller");
        VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleCounter.class);
        vertx.eventBus().send(BACKOFFICE_CONTROLLER_EVENTBUS, VertxJsonMapper.mapInBodyFrom(vehicleCounter));
        Log.log("RabbitmqListenerUpdate received updates and send to controller");
    }
}
