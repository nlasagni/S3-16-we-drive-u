package com.wedriveu.services.vehicle.available.control;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.vehicle.finder.control.VehicleFinder;
import com.wedriveu.services.vehicle.finder.control.VehicleFinderImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Marco on 02/08/2017.
 */
public class AvailableControlImpl extends AbstractVerticle implements AvailableControl {

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
        eventBus.consumer(Messages.AvailableControl.AVAILABLE_COMPLETED, this::availableVehiclesCompleted);
    }

    @Override
    public void requestAvailableVehicles(Message message) {
        userData = (JsonObject) message.body();

        deployVehicleStore();


        /*JsonObject json = (JsonObject) message.body();
        userLat = json.getString(Constants.USER_LATITUDE);
        userLon = json.getString(Constants.USER_LONGITUDE);
        destinationLat = json.getString(Constants.DESTINATION_LATITUDE);
        destinationLon = json.getString(Constants.DESTINATION_LONGITUDE);
        username = json.getString(Constants.USER_USERNAME);*/
    }

    private void deployVehicleStore() {
        vertx.deployVerticle(new VehicleStoreImpl(), vehicleStoreDeployed -> {
            if (vehicleStoreDeployed.succeeded()) {
                //DeploymentOptions options = new DeploymentOptions().setWorker(true);
                eventBus.send(Messages.AvailableControl.AVAILABLE_REQUEST, null);
            } else {
                Log.error(Constants.DEPLOY_ERROR, VehicleStoreImpl.class.getSimpleName(), vehicleStoreDeployed.cause());
            }

        });
    }

    @Override
    public void availableVehiclesCompleted(Message message) {
        JsonArray availableVehicles = (JsonArray) message.body();
        userData.put(Constants.AVAILABLE_VEHICLES, availableVehicles);
        Log.info("USER_DATA", userData.encodePrettily());
        try {
            deployVehicleFinder();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void deployVehicleFinder() throws IOException, TimeoutException {

        VehicleFinder vehicleFinder = new VehicleFinderImpl();
        vertx.deployVerticle((Verticle) vehicleFinder, finderDeployed ->
                this.eventBus.send(Messages.AvailableControl.DATA_TO_VEHICLE, userData));
        //TODO wrappare in un unico json object i veicoli e i dati presi dall'utente
    }
}
