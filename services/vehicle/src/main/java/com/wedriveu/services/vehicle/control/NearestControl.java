package com.wedriveu.services.vehicle.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.message.VehicleResponseCanDrive;
import com.wedriveu.services.vehicle.boundary.nearest.VehicleFinderVerticle;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.wedriveu.services.shared.model.Vehicle.NO_ELIGIBLE_VEHICLE_RESPONSE;
import static com.wedriveu.shared.util.Constants.*;
import static com.wedriveu.shared.util.Constants.Vehicle.LICENSE_PLATE;
import static com.wedriveu.shared.util.Constants.Vehicle.SPEED;


/**
 * This Controller serves some interaction requests with the
 * {@linkplain com.wedriveu.services.vehicle.entity.VehicleStore} database. It handles the database replies
 * by deploying and interacting with other Verticles.
 *
 * @author Marco Baldassarri on 02/08/2017.
 */
public class NearestControl extends AbstractVerticle {

    private EventBus eventBus;
    private static final String AVAILABLE_COMPLETED_FOR_SUBSTITUTION = "store.available.completed.substitution";
    private static final String NEAREST_VEHICLE_FOR_SUBSTITUTION = "nearest.vehicle.for.substitution";

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.VehicleStore.AVAILABLE_COMPLETED, this::availableVehiclesCompleted);
        eventBus.consumer(Messages.VehicleFinder.VEHICLE_RESPONSE, this::handleVehicleResponses);
        eventBus.consumer(Messages.VehicleFinder.NO_VEHICLE, this::handleNoVehicle);
        eventBus.consumer(AVAILABLE_COMPLETED_FOR_SUBSTITUTION, this::availableVehiclesCompletedForSubstitution);
    }

    private void undeployFinder(String deploymentId) {
        if (deploymentId != null && !deploymentId.isEmpty()) {
            vertx.undeploy(deploymentId);
        }
    }

    private void handleNoVehicle(Message message) {
        JsonObject response = (JsonObject) message.body();
        String deploymentId = response.getString(Constants.EventBus.DEPLOYMENT_ID);
        undeployFinder(deploymentId);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(USERNAME, response.getString(USERNAME));
        Vehicle noEligibleVehicle = new Vehicle();
        noEligibleVehicle.setNotEligibleVehicleFound(NO_ELIGIBLE_VEHICLE_RESPONSE);
        jsonObject.put(VEHICLE, JsonObject.mapFrom(noEligibleVehicle).toString());
        vertx.eventBus().send(Messages.VehicleStore.GET_VEHICLE_COMPLETED_NEAREST, jsonObject);
    }

    private void handleVehicleResponses(Message message) {
        JsonObject response = (JsonObject) message.body();
        String deploymentId = response.getString(Constants.EventBus.DEPLOYMENT_ID);
        undeployFinder(deploymentId);
        JsonArray responseListJson = response.getJsonArray(Messages.VehicleFinder.VEHICLE_RESPONSE_RESULT);
        handleEligibleVehicles(responseListJson);
    }

    private void handleEligibleVehicles(JsonArray responseListJson) {
        List<VehicleResponseCanDrive> responseList = new ArrayList<>(responseListJson.size());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            VehicleResponseCanDrive[] vehicleResponsCanDrives =
                    objectMapper.readValue(responseListJson.toString(), VehicleResponseCanDrive[].class);
            responseList = Arrays.asList(vehicleResponsCanDrives);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendReplyToUser(getEligibleVehicles(responseList));
    }

    private void sendReplyToUser(List<VehicleResponseCanDrive> eligibleVehicles) {

        vertx.eventBus().send(Messages.NearestControl.GET_VEHICLE_NEAREST, getBestVehicle(eligibleVehicles));
    }

    private List<VehicleResponseCanDrive> getEligibleVehicles(List<VehicleResponseCanDrive> responseList) {
        return responseList.stream().filter(x -> x.isEligible()).collect(Collectors.toList());
    }

    private JsonObject getBestVehicle(List<VehicleResponseCanDrive> responseList) {
        responseList.sort(Comparator.comparing(v ->
                v.getDistanceToUser() / v.getVehicleSpeed()
        ));
        VehicleResponseCanDrive chosen = responseList.get(ZERO);
        JsonObject bestVehicleJson = new JsonObject();
        bestVehicleJson.put(USERNAME, chosen.getUsername());
        bestVehicleJson.put(LICENSE_PLATE, chosen.getLicencePlate());
        bestVehicleJson.put(SPEED, chosen.getVehicleSpeed());
        bestVehicleJson.put(Trip.DISTANCE_TO_USER, chosen.getDistanceToUser());
        bestVehicleJson.put(Trip.TOTAL_DISTANCE, chosen.getTotalDistance());
        return bestVehicleJson;
    }

    private void availableVehiclesCompleted(Message message) {
        //TODO
        Log.info(this.getClass().getSimpleName(), "NEAREST CONTROL availableVehiclesCompleted");
        UUID uniqueKey = UUID.randomUUID();
        JsonObject userData = (JsonObject) message.body();
        vertx.deployVerticle(new VehicleFinderVerticle(uniqueKey.toString()), completed -> {
            if (completed.succeeded()) {
                this.eventBus.send(Messages.NearestControl.DATA_TO_VEHICLE, userData);
            }
        });
    }

    private void availableVehiclesCompletedForSubstitution(Message message) {
        UUID uniqueKey = UUID.randomUUID();
        JsonObject userData = (JsonObject) message.body();
        vertx.deployVerticle(new VehicleFinderVerticle(uniqueKey.toString()), completed -> {
            if (completed.succeeded()) {
                this.eventBus.send(NEAREST_VEHICLE_FOR_SUBSTITUTION, userData);
            }
        });
    }

}


