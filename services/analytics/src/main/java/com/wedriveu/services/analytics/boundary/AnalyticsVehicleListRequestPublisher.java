package com.wedriveu.services.analytics.boundary;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import static com.wedriveu.shared.util.Constants.*;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLE_REQUEST_ALL;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleListRequestPublisher extends VerticlePublisher {

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
        vertx.eventBus().consumer(ANALYTICS_VEHICLE_LIST_REQUEST_EVENTBUS, this::requestVehicleListToVehicleService);
    }

    private void requestVehicleListToVehicleService(Message message) {
        JsonObject dataToUser = new JsonObject();
        dataToUser.put(EventBus.BODY, VEHICLE_REQUEST_ALL_MESSAGE);
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,ANALYTICS_VEHICLE_REQUEST_ALL,dataToUser, published -> { });
    }
}
