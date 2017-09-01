package com.wedriveu.backoffice.analytics;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_RESPONSE_VEHICLE_COUNTER;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleCounterResponseGenerator extends VerticlePublisher {
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
            vertx.eventBus().consumer(ConstantsBackOffice.EventBus.BACKOFFICE_VEHICLE_COUNTER_RESPONSE_GENERATOR_START_TEST, this::sendVehicleCounterToBackOffice);
        }

        private void sendVehicleCounterToBackOffice(Message message) {
            JsonObject dataToUser = VertxJsonMapper.mapInBodyFrom(
                    VehicleCounterGeneratorFactory.getVehicleCounter(
                            ConstantsBackOffice.VEHICLE_AVAILABLE,
                            ConstantsBackOffice.VEHICLE_BOOKED,
                            ConstantsBackOffice.VEHICLE_BROKEN,
                            ConstantsBackOffice.VEHICLE_NETWORK_ISSUES,
                            ConstantsBackOffice.VEHICLE_RECHARGING));
            publish(Constants.RabbitMQ.Exchanges.ANALYTICS, ANALYTICS_RESPONSE_VEHICLE_COUNTER,
                    dataToUser,
                    res -> {
                    });
        }
}
