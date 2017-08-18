package com.wedriveu.services.analytics.control;

import com.wedriveu.services.analytics.entity.AnalyticsStore;
import com.wedriveu.services.analytics.entity.AnalyticsStoreImpl;
import com.wedriveu.services.analytics.entity.MessageVehicleCounterWithID;
import com.wedriveu.services.analytics.entity.VehiclesCounterAlgorithmImpl;
import com.wedriveu.services.shared.model.AnalyticsVehicle;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.model.VehicleListObject;
import com.wedriveu.services.shared.store.JsonFileEntityListStoreStrategyImpl;
import com.wedriveu.shared.util.Log;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.*;


/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleDataManipulationVerticle extends AbstractVerticle{
    private AnalyticsStore analyticsStore;

    @Override
    public void start() throws Exception{
        analyticsStore = new AnalyticsStoreImpl(
                new JsonFileEntityListStoreStrategyImpl<>(AnalyticsVehicle.class, "AnalyticsStore"),
                new VehiclesCounterAlgorithmImpl());
        vertx.eventBus().consumer(ANALYTICS_CONTROLLER_VEHICLE_LIST_EVENTBUS, this::convertVehicleList);
        vertx.eventBus().consumer(ANALYTICS_VEHICLE_COUNTER_REQUEST_EVENTBUS, this::handleVehicleCounterRequest);
        vertx.eventBus().consumer(ANALYTICS_VEHICLE_STORE_UPDATE_REQUEST_EVENTBUS, this::updateVehicleStore);
        Log.log("future AnalyticsVehicleDataManipulationVerticle complete");
    }

    private void convertVehicleList(Message message) {
        addVehiclesToDatabase(VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), VehicleListObject.class));
        sendVehicleUpdates();
    }

    private void addVehiclesToDatabase(VehicleListObject vehicleList) {
        for(Vehicle vehicle: vehicleList.getVehicleList()){
            analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getStatus());
        }
    }

    private void handleVehicleCounterRequest(Message message) {
        String backofficeID = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), String.class);
        vertx.eventBus().send(ANALYTCS_VEHICLE_COUNTER_UPDATE_EVENTBUS,
                VertxJsonMapper.mapInBodyFrom(new MessageVehicleCounterWithID(
                        backofficeID,
                        analyticsStore.getVehicleCounter())));

    }

    private void updateVehicleStore(Message message) {
        Vehicle vehicle = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), Vehicle.class);
        if (analyticsStore.getVehicleByLicensePlate(vehicle.getLicensePlate()) != null) {
            analyticsStore.updateVehicle(vehicle.getLicensePlate(), vehicle.getStatus());
        } else {
            analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getStatus());
        }
        sendVehicleUpdates();
    }

    private void sendVehicleUpdates() {
        vertx.eventBus().send(ANALYTCS_VEHICLE_COUNTER_UPDATE_EVENTBUS,
                VertxJsonMapper.mapInBodyFrom(new MessageVehicleCounterWithID(
                        "",
                        analyticsStore.getVehicleCounter())));
    }

}
