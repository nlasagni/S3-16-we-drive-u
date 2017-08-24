package com.wedriveu.services.analytics.control;

import com.wedriveu.services.analytics.entity.AnalyticsStore;
import com.wedriveu.services.analytics.entity.AnalyticsStoreImpl;
import com.wedriveu.services.analytics.entity.MessageVehicleCounterWithID;
import com.wedriveu.services.analytics.entity.VehiclesCounterAlgorithmImpl;
import com.wedriveu.services.analytics.util.EventBus;
import com.wedriveu.services.shared.model.AnalyticsVehicle;
import com.wedriveu.services.shared.model.AnalyticsVehicleList;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.store.JsonFileEntityListStoreStrategyImpl;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Optional;


/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsVehicleDataManipulationVerticle extends AbstractVerticle {
    private AnalyticsStore analyticsStore;

    private static final String DATABASE_FILE_NAME = "analytics.json";

    @Override
    public void start() throws Exception {
        analyticsStore = new AnalyticsStoreImpl(
                new JsonFileEntityListStoreStrategyImpl<>(AnalyticsVehicle.class, DATABASE_FILE_NAME),
                new VehiclesCounterAlgorithmImpl());
        vertx.eventBus().consumer(EventBus.CONTROLLER_VEHICLE_LIST, this::convertVehicleList);
        vertx.eventBus().consumer(EventBus.VEHICLE_COUNTER_REQUEST, this::handleVehicleCounterRequest);
        vertx.eventBus().consumer(EventBus.VEHICLE_STORE_UPDATE_REQUEST, this::updateVehicleStore);
    }

    private void convertVehicleList(Message message) {
        addVehiclesToDatabase(VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), AnalyticsVehicleList.class));
        sendVehicleUpdates();
    }

    private void addVehiclesToDatabase(AnalyticsVehicleList vehicleList) {
        analyticsStore.clear();
        for (Vehicle vehicle : vehicleList.getVehicleList()) {
            analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getStatus());
        }
        sendVehicleUpdates();
    }

    private void handleVehicleCounterRequest(Message message) {
        JsonObject dataToUser = new JsonObject(message.body().toString());
        String backofficeId = dataToUser.getValue(Constants.EventBus.BODY).toString();
        vertx.eventBus().send(EventBus.VEHICLE_COUNTER_RESPONSE,
                VertxJsonMapper.mapInBodyFrom(new MessageVehicleCounterWithID(
                        backofficeId,
                        analyticsStore.getVehicleCounter())));

    }

    private void updateVehicleStore(Message message) {
        UpdateToService vehicle = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), UpdateToService.class);
        Optional<AnalyticsVehicle> old = analyticsStore.getVehicleByLicensePlate(vehicle.getLicense());
        boolean sendUpdate = true;
        if (!old.isPresent()) {
            analyticsStore.updateVehicle(vehicle.getLicense(), vehicle.getStatus());
        } else {
            if (!old.get().getStatus().equals(vehicle.getStatus())) {
                analyticsStore.addVehicle(vehicle.getLicense(), vehicle.getStatus());
            }else {
                sendUpdate = false;
            }
        }
        if (sendUpdate) {
            sendVehicleUpdates();
        }
    }

    private void sendVehicleUpdates() {
        vertx.eventBus().send(EventBus.VEHICLE_COUNTER_UPDATE,
                VertxJsonMapper.mapInBodyFrom(new MessageVehicleCounterWithID(
                        "",
                        analyticsStore.getVehicleCounter())));
    }

}
