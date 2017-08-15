package com.wedriveu.services.analytics.control;

import com.wedriveu.services.analytics.entity.AnalyticsStore;
import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.entity.VehicleListObject;
import com.wedriveu.services.shared.util.Log;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.ANALYTICS_CONTROLLER_VEHICLE_LIST_VERTICLE_ADDRESS;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVerticleController extends AbstractVerticle {
    private AnalyticsStore analyticsStore;
    @Override
    public void start() throws Exception{
        vertx.eventBus().consumer(ANALYTICS_CONTROLLER_VEHICLE_LIST_VERTICLE_ADDRESS, this::storeVehicleList);
        Log.log("future AnalyticsVerticleController complete");
    }

    private void storeVehicleList(Message message) {
        VehicleListObject vehicleList = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleListObject.class);
        for(Vehicle vehicle: vehicleList.getVehicleList()){
            analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getStatus());
        }
    }
}
