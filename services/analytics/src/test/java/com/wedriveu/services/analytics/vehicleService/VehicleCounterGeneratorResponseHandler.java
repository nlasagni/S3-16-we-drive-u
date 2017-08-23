package com.wedriveu.services.analytics.vehicleService;

import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLE_LIST;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleCounterGeneratorResponseHandler  extends VerticleConsumer{

        public VehicleCounterGeneratorResponseHandler() {
            super(Constants.RabbitMQ.Exchanges.ANALYTICS +"."+ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLE_LIST +"." + EventBus.Messages.ANALYTICS_VEHICLE_COUNTER_TEST_BACKOFFICE_ID +".test");
        }

        @Override
        public void start(Future futureRetriever) throws Exception {
            super.start();
            Future<Void> futureConsumer = Future.future();
            futureConsumer.setHandler(v -> {
                if (v.succeeded()) {
                    futureRetriever.complete();
                } else {
                    futureRetriever.fail(v.cause());
                }
            });

            startConsumerWithFuture(Constants.RabbitMQ.Exchanges.ANALYTICS,
                    ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLE_LIST +"." + EventBus.Messages.ANALYTICS_VEHICLE_COUNTER_TEST_BACKOFFICE_ID,
                    EventBus.AVAILABLE_ADDRESS_FAKE_VEHICLE_COUNTER_RESPONSE,
                    futureConsumer);
        }

        @Override
        public void registerConsumer(String eventBus) {
            vertx.eventBus().consumer(eventBus, this::sendUpdatesToController);
        }

        private void sendUpdatesToController(Message message) {
            VehicleCounter vehicleCounter = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleCounter.class);
            vertx.eventBus().send(EventBus.FAKE_VEHICLE_COUNTER_RESPONSE_TEST_EVENTBUS, VertxJsonMapper.mapInBodyFrom(vehicleCounter));
        }
}
