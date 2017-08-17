package com.wedriveu.services.analytics.vehicleServiceFake;

import com.wedriveu.services.shared.entity.VehicleListObject;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLES_RESPONSE_ALL;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleUpdaterFake extends VerticlePublisher {
    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(Constants.ANALYTICS_EVENTBUS_AVAILABLE_ADDRESS_VEHICLE_UPDATE_HANDLER, this::sendUpdates);
    }

    private void sendUpdates(Message message) {
        UpdateToService updateToService = new UpdateToService();
        updateToService.setLicense("Veicolo1");
        updateToService.setStatus("broken");
        JsonObject vehicleListJson = VertxJsonMapper.mapInBodyFrom(updateToService);
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE,vehicleListJson, published -> { });
    }
}
