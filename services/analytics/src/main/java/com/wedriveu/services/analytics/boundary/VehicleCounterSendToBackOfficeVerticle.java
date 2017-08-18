package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.entity.MessageVehicleCounterWithID;
import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
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
public class VehicleCounterSendToBackOfficeVerticle extends VerticlePublisher{
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future future = Future.future();
        super.start(future);
        future.setHandler(res-> {
            startFuture.complete();
            startConsumer();
        });
    }

    private void startConsumer() {
        vertx.eventBus().consumer(ANALYTICS_VEHICLE_COUNTER_RESPONSE_EVENTBUS, this::sendVehicleCounterToBackOffice);
    }

    private void sendVehicleCounterToBackOffice(Message message) {
        MessageVehicleCounterWithID messageVehicleCounterWithID = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), MessageVehicleCounterWithID.class);
        JsonObject dataToUser = VertxJsonMapper.mapInBodyFrom(messageVehicleCounterWithID.getVehicleCounter());
        if (messageVehicleCounterWithID.getBackofficeID().equals(""))   {
            System.out.println("sono qua");
            publish(Constants.RabbitMQ.Exchanges.ANALYTICS,ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLES ,dataToUser, null);
        } else {
            System.out.println("sono qua 2");
            publish(Constants.RabbitMQ.Exchanges.ANALYTICS,ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLES + "." + messageVehicleCounterWithID.getBackofficeID(),dataToUser, res-> {
                if (res.succeeded()) {
                    Log.log("succeded");
                }
                else {
                    Log.error("error", res.cause().getLocalizedMessage(), res.cause());
                }
            });
        }
    }
}
