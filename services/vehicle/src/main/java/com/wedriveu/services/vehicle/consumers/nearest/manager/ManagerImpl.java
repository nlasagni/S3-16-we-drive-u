package com.wedriveu.services.vehicle.consumers.nearest.manager;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.app.Messages;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.vehicle.finder.control.VehicleFinder;
import com.wedriveu.services.vehicle.finder.control.VehicleFinderImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Marco on 02/08/2017.
 */
public class ManagerImpl extends AbstractVerticle implements Manager{

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
        eventBus.consumer(Messages.Store.AVAILABLE_COMPLETED, this::availableVehiclesCompleted);
    }

    @Override
    public void requestAvailableVehicles(Message message) {

        vertx.deployVerticle(new VehicleStoreImpl(), vehicleStoreDeployed -> {
            if (vehicleStoreDeployed.succeeded()) {
                DeploymentOptions options = new DeploymentOptions().setWorker(true);
                eventBus.send(Messages.Store.AVAILABLE_REQUEST, options);
            } else {
                Log.error(Constants.DEPLOY_ERROR, VehicleStoreImpl.class.getSimpleName(), vehicleStoreDeployed.cause());
            }

        });

        userData = (JsonObject) message.body();
        /*JsonObject json = (JsonObject) message.body();
        userLat = json.getString(Constants.USER_LATITUDE);
        userLon = json.getString(Constants.USER_LONGITUDE);
        destinationLat = json.getString(Constants.DESTINATION_LATITUDE);
        destinationLon = json.getString(Constants.DESTINATION_LONGITUDE);
        username = json.getString(Constants.USER_USERNAME);*/
    }

    @Override
    public void availableVehiclesCompleted(Message message) {
        userData.put(Constants.AVAILABLE_VEHICLES, message.body());
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
                this.eventBus.send(Messages.Finder.USER_DATA, userData));
        //TODO wrappare in un unico json object i veicoli e i dati presi dall'utente
    }
}
