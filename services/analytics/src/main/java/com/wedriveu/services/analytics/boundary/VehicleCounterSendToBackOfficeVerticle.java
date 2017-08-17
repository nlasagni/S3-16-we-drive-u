package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.entity.MessageVehicleCounterWithID;
import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleCounterSendToBackOfficeVerticle extends VerticlePublisher{
    @Override
    public void start() throws Exception {
        startConsumer();
    }

    private void startConsumer() {
        vertx.eventBus().consumer(ANALYTICS_VEHICLE_COUNTER_RESPONSE_EVENTBUS, this::sendVehicleCounterToBackOffice);
    }

    private void sendVehicleCounterToBackOffice(Message message) {
        MessageVehicleCounterWithID messageVehicleCounterWithID = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), MessageVehicleCounterWithID.class);
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(Constants.EventBus.BODY, messageVehicleCounterWithID.getVehicleCounter());
        publish(Constants.RabbitMQ.Exchanges.ANALYTICS,ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLES + "." + messageVehicleCounterWithID.getBackofficeID(),dataToUser, null);
    }
}
