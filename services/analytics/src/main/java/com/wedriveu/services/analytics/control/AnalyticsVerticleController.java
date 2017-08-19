package com.wedriveu.services.analytics.control;

import com.wedriveu.services.analytics.entity.AnalyticsStore;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.model.VehicleListObject;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.ANALYTICS_CONTROLLER_VEHICLE_LIST_EVENTBUS;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVerticleController extends AbstractVerticle {
    private AnalyticsStore analyticsStore;
    @Override
    public void start() throws Exception{
        vertx.eventBus().consumer(ANALYTICS_CONTROLLER_VEHICLE_LIST_EVENTBUS, this::storeVehicleList);
        Log.info("future AnalyticsVerticleController complete");
    }

    private void storeVehicleList(Message message) {
        VehicleListObject vehicleList = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleListObject.class);
        for(Vehicle vehicle: vehicleList.getVehicleList()){
            analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getStatus());
        }
    }
}
