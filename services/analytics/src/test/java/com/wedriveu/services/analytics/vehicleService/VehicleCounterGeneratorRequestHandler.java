package com.wedriveu.services.analytics.vehicleService;


import com.wedriveu.services.analytics.util.ConstantsAnalytics;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_REQUEST_VEHICLE_COUNTER;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleCounterGeneratorRequestHandler extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future future = Future.future();
        super.start(future);
        future.setHandler(res -> {
            vertx.eventBus().consumer(ConstantsAnalytics.EventBus.AVAILABLE_ADDRESS_FAKE_VEHICLE_COUNTER_REQUEST, this::requestVehicleCounterToAnalytics);
            startFuture.complete();
        });

    }

    private void requestVehicleCounterToAnalytics(Message message) {
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(com.wedriveu.shared.util.Constants.EventBus.BODY, ConstantsAnalytics.Messages.ANALYTICS_VEHICLE_COUNTER_TEST_BACKOFFICE_ID);
        publish(com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.ANALYTICS, ANALYTICS_REQUEST_VEHICLE_COUNTER, dataToUser, published -> {
        });
    }
}
