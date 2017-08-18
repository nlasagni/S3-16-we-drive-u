package com.wedriveu.services.analytics.vehicleServiceFake;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.model.VehicleListObject;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.shared.util.Position;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Date;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ANALYTICS_VEHICLES_RESPONSE_ALL;

/**
 * @author Stefano Bernagozzi
 */
public class VehicleListGeneratorResponseHandler extends VerticlePublisher{
    @Override
    public void start() throws Exception {
        startConsumer();
        Log.log("future VehicleListGeneratorResponseHandler complete");
    }

    private void startConsumer() {
        Log.log("started vertx eventbus consumer in VehicleListGeneratorResponseHandler, attending start to receive");
        vertx.eventBus().consumer("mandaVeicoli", this::sendVehicleListToAnalyticsService);
    }


    private void sendVehicleListToAnalyticsService(Message message) {
        ArrayList<Vehicle> vehicleList = new ArrayList<>();
        vehicleList.add(new Vehicle("MACCHINA1",
                    "broken",
                    new Position(10.2, 13.2),
                    new Date(2017, 11, 30, 12, 37, 43)));
        vehicleList.add(new Vehicle("MACCHINA2",
                    "available",
                    new Position(11.2, 14.2),
                    new Date(2017, 10, 28, 11, 43, 12)));
        vehicleList.add(new Vehicle("MACCHINA3",
                    "busy",
                    new Position(15.2, 13.2),
                    new Date(2017, 9, 26, 10, 56, 46)));
        vehicleList.add(new Vehicle("MACCHINA4",
                    "recharging",
                    new Position(13.2, 16.2),
                    new Date(2017, 8, 24, 9, 37, 22)));
        JsonObject vehicleListJson = VertxJsonMapper.mapInBodyFrom(new VehicleListObject(vehicleList));
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,ANALYTICS_VEHICLES_RESPONSE_ALL,vehicleListJson, published -> { });
        Log.log("sent request for all vehicles to vehicle service in VehicleListGeneratorResponseHandler");
    }

}
