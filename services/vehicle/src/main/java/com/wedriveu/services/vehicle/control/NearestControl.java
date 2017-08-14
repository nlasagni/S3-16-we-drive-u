package com.wedriveu.services.vehicle.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.rabbitmq.nearest.VehicleResponseCanDrive;
import com.wedriveu.services.vehicle.boundary.nearest.VehicleFinderVerticle;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.wedriveu.services.shared.entity.Vehicle.NO_ELIGIBLE_VEHICLE_RESPONSE;
import static com.wedriveu.shared.utils.Constants.USERNAME;
import static com.wedriveu.shared.utils.Constants.Vehicle.LICENCE_PLATE;
import static com.wedriveu.shared.utils.Constants.ZERO;


/**
 * This Controller serves all the interaction requests with the
 * {@linkplain com.wedriveu.services.vehicle.entity.VehicleStore} database. It handles the database replies
 * by deploying and interacting with other Verticles.
 *
 * @author Marco Baldassarri on 02/08/2017.
 */
public class NearestControl extends AbstractVerticle {

    private EventBus eventBus;
    private List<VehicleResponseCanDrive> responseList;

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.VehicleStore.AVAILABLE_COMPLETED, this::availableVehiclesCompleted);
        eventBus.consumer(Messages.VehicleFinder.VEHICLE_RESPONSE, this::handleVehicleResponses);
    }

    private void handleVehicleResponses(Message message) {
        JsonArray responseListJson = (JsonArray) message.body();
        responseList = new ArrayList<>(responseListJson.size());
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
        if(eligibleVehicles.isEmpty() || eligibleVehicles == null) {
            Vehicle noEligibleVehicle = new Vehicle();
            noEligibleVehicle.setNotEligibleVehicleFound(NO_ELIGIBLE_VEHICLE_RESPONSE);
            vertx.eventBus().send(Messages.VehicleStore.GET_VEHICLE_COMPLETED, noEligibleVehicle);
        } else {
            vertx.eventBus().send(Messages.NearestControl.GET_VEHICLE, getBestVehicle(responseList));
        }
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
        bestVehicleJson.put(LICENCE_PLATE, chosen.getLicencePlate());
        return bestVehicleJson;
    }

    private void availableVehiclesCompleted(Message message) {
        JsonObject userData = (JsonObject) message.body();
        vertx.deployVerticle(new VehicleFinderVerticle(), completed -> {
            if (completed.succeeded()) {
                this.eventBus.send(Messages.NearestControl.DATA_TO_VEHICLE, userData);
            }
        });
    }

}


