package com.wedriveu.services.analytics.vehicleService;

import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.analytics.util.*;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLES_RESPONSE_ALL;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListGeneratorResponseHandler extends VerticlePublisher {
    @Override
    public void start(Future startFuture) throws Exception {
        Future future = Future.future();
        super.start(future);
        future.setHandler(res -> {
            startFuture.complete();
            startConsumer();
        });
    }

    private void startConsumer() {
        vertx.eventBus().consumer(ConstantsAnalytics.EventBus.TEST_VEHICLE_LIST_RESPONSE, this::sendVehicleListToAnalyticsService);
    }


    private void sendVehicleListToAnalyticsService(Message message) {
        List<Vehicle> vehicleList = VehicleListGenerator.getVehicleList();
        JsonObject vehicleListJson = VertxJsonMapper.mapInBodyFrom(new AnalyticsVehicleList(vehicleList));
        publish(com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.VEHICLE, ANALYTICS_VEHICLES_RESPONSE_ALL, vehicleListJson, published -> { });
    }

}
