package com.wedriveu.services.analytics.vehicleService;

import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLES_RESPONSE_ALL;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleUpdateGenerator extends VerticlePublisher {
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
        vertx.eventBus().consumer(EventBus.TEST_VEHICLE_UPDATE, this::sendVehicleUpdateToAnalyticsService);
    }


    private void sendVehicleUpdateToAnalyticsService(Message message) {
        UpdateToService updateToService = new UpdateToService();
        updateToService.setLicense(EventBus.Messages.ANALYTICS_VEHICLE_TEST_LICENSE_PLATE);
        updateToService.setStatus(Vehicle.STATUS_BOOKED);
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE,
                VertxJsonMapper.mapInBodyFrom(updateToService),
                res-> { });
    }

}
