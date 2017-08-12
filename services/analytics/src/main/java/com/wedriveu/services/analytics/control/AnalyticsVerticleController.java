package com.wedriveu.services.analytics.control;

import com.wedriveu.services.analytics.boundary.VehicleListObject;
import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVerticleController extends AbstractVerticle {
    @Override
    public void start() throws Exception{
        vertx.eventBus().consumer(ANALYTICS_CONTROLLER_VEHICLE_LIST_VERTICLE_ADDRESS, this::storeVehicleList);
        Log.log("future AnalyticsVerticleController complete");
    }

    private void storeVehicleList(Message message) {
        System.out.println("AnalyticsVerticleController " + message.body().toString());
        VehicleListObject vehicleList = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleListObject.class);
        for(Vehicle vehicle: vehicleList.getVehicleList()){
            System.out.println(vehicle.toString());
        }
/*
        for (Vehicle vehicle: vehicleList) {
            System.out.println(vehicle.toString());
        }
        */
    }
}
