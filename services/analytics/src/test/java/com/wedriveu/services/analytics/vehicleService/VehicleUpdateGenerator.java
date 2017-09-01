package com.wedriveu.services.analytics.vehicleService;

import com.wedriveu.services.analytics.util.ConstantsAnalytics;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;

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
        vertx.eventBus().consumer(ConstantsAnalytics.EventBus.TEST_VEHICLE_UPDATE, this::sendVehicleUpdateToAnalyticsService);
    }


    private void sendVehicleUpdateToAnalyticsService(Message message) {
        UpdateToService updateToService = new UpdateToService();
        updateToService.setLicense(ConstantsAnalytics.Messages.ANALYTICS_VEHICLE_TEST_LICENSE_PLATE);
        updateToService.setStatus(Vehicle.STATUS_BOOKED);
        publish(com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.VEHICLE,
                com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE,
                VertxJsonMapper.mapInBodyFrom(updateToService),
                res-> { });
    }

}
