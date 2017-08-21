package com.wedriveu.services.analytics.vehicleService;

import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleUpdater extends VerticlePublisher {
    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(EventBus.AVAILABLE_ADDRESS_VEHICLE_UPDATE_HANDLER,
                this::sendUpdates);
    }

    private void sendUpdates(Message message) {
        UpdateToService updateToService = new UpdateToService();
        updateToService.setLicense("Veicolo1");
        updateToService.setStatus(Vehicle.STATUS_BROKEN_STOLEN);
        JsonObject vehicleListJson = VertxJsonMapper.mapInBodyFrom(updateToService);
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE,
                vehicleListJson,
                published -> {
        });
    }
}
