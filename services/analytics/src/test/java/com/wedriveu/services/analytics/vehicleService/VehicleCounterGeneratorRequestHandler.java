package com.wedriveu.services.analytics.vehicleService;


import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ROUTING_KEY_ANALYTICS_REQUEST_VEHICLE_LIST;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleCounterGeneratorRequestHandler extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future future = Future.future();
        super.start(future);
        future.setHandler(res -> {
            vertx.eventBus().consumer(EventBus.AVAILABLE_ADDRESS_FAKE_VEHICLE_COUNTER_REQUEST, this::requestVehicleCounterToAnalytics);
            startFuture.complete();
        });

    }

    private void requestVehicleCounterToAnalytics(Message message) {
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(Constants.EventBus.BODY, EventBus.Messages.ANALYTICS_VEHICLE_COUNTER_TEST_BACKOFFICE_ID);
        publish(Constants.RabbitMQ.Exchanges.ANALYTICS, ROUTING_KEY_ANALYTICS_REQUEST_VEHICLE_LIST, dataToUser, published -> {
        });
    }
}
