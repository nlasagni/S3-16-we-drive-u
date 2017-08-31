package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.analytics.entity.MessageVehicleCounterWithID;
import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_RESPONSE_VEHICLE_COUNTER;

/**
 * a verticle for publishing the vehicle counter to the backoffice
 *
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehiclesResponsePublisher extends VerticlePublisher {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future future = Future.future();
        super.start(future);
        future.setHandler(res -> {
            startFuture.complete();
            startConsumer();
        });
    }

    private void startConsumer() {
        vertx.eventBus().consumer(EventBus.VEHICLE_COUNTER_RESPONSE, this::sendVehicleCounterToBackOffice);
    }

    private void sendVehicleCounterToBackOffice(Message message) {
        MessageVehicleCounterWithID messageVehicleCounterWithID =
                VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), MessageVehicleCounterWithID.class);
        JsonObject dataToUser = VertxJsonMapper.mapInBodyFrom(messageVehicleCounterWithID.getVehicleCounter());
        if (messageVehicleCounterWithID.getBackofficeID().equals("")) {
            publish(Constants.RabbitMQ.Exchanges.ANALYTICS,
                    ANALYTICS_RESPONSE_VEHICLE_COUNTER,
                    dataToUser,
                    res -> { });
        } else {
            publish(Constants.RabbitMQ.Exchanges.ANALYTICS,
                    ANALYTICS_RESPONSE_VEHICLE_COUNTER + "." + messageVehicleCounterWithID.getBackofficeID(),
                    dataToUser,
                    res -> { });
        }
    }
}
