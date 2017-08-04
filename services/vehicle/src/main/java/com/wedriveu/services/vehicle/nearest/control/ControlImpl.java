package com.wedriveu.services.vehicle.nearest.control;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.app.Messages;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Marco on 02/08/2017.
 */
public class ControlImpl extends AbstractVerticle implements Control {

    private EventBus eventBus;
    private JsonObject userData;
    private String userLat;
    private String userLon;
    private String destinationLat;
    private String destinationLon;
    private String username;

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.NearestConsumer.AVAILABLE, this::requestAvailableVehicles);
        eventBus.consumer(Messages.VehicleStore.AVAILABLE_COMPLETED, this::availableVehiclesCompleted);
    }

    @Override
    public void requestAvailableVehicles(Message message) {
        userData = (JsonObject) message.body();
        //DeploymentOptions options = new DeploymentOptions().setWorker(true);
        eventBus.send(Messages.AvailableControl.AVAILABLE_REQUEST, null);
    }


    @Override
    public void availableVehiclesCompleted(Message message) {
        JsonArray availableVehicles = (JsonArray) message.body();
        userData.put(Constants.AVAILABLE_VEHICLES, availableVehicles);
        Log.info("USER_DATA", userData.encodePrettily());
        deployVehicleFinder();

    }


    @Override
    public void deployVehicleFinder() {
        this.eventBus.send(Messages.AvailableControl.DATA_TO_VEHICLE, userData);
    }
}
