package com.wedriveu.services.vehicle.nearest.control;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.shared.utilities.PositionUtils;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.entity.VehicleResponse;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.vehicle.nearest.boundary.finder.VehicleFinder;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.wedriveu.services.shared.utilities.Constants.USER_LATITUDE;
import static com.wedriveu.services.shared.utilities.Constants.USER_LONGITUDE;

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

    private List<VehicleResponse> responseList;

    @Override
    public void start(Future<Void> future) throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.UserNearestConsumer.AVAILABLE, this::requestAvailableVehicles);
        eventBus.consumer(Messages.VehicleStore.AVAILABLE_COMPLETED, this::availableVehiclesCompleted);
        eventBus.consumer(Messages.NearestControl.DATA_TO_VEHICLE, this::chooseBestVehicle);
        future.complete();
    }

    private void chooseBestVehicle(Message message) {
        JsonArray eligibleVehicles = (JsonArray) message.body();
        responseList = new ArrayList<>(eligibleVehicles.size());
        responseList = eligibleVehicles.getList();
        getBestVehicle(responseList);


    }

    private void getBestVehicle(List<VehicleResponse> responseList) {
        //TODO choose the best vehicle based on speed and distance to the user
    }

    @Override
    public void requestAvailableVehicles(Message message) {
        userData = (JsonObject) message.body();
        //DeploymentOptions options = new DeploymentOptions().setWorker(true);
        vertx.deployVerticle(new VehicleStoreImpl(), completed -> {
            if(completed.succeeded()){
                eventBus.send(Messages.NearestControl.AVAILABLE_REQUEST, userData);
            } else {
                Log.error(Constants.DEPLOY_ERROR, ControlImpl.class.getSimpleName(), completed.cause());
            }
        });

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
        vertx.deployVerticle(new VehicleFinder(), completed -> {
            if(completed.succeeded()) {
                this.eventBus.send(Messages.NearestControl.DATA_TO_VEHICLE, userData);
            } else {
                Log.error(Constants.DEPLOY_ERROR, ControlImpl.class.getSimpleName(), completed.cause());
            }
        });

    }
}
