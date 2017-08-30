package com.wedriveu.services.vehicle.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.boundary.nearest.VehicleFinderVerticle;
import com.wedriveu.services.vehicle.entity.VehicleResponseCanDrive;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.AbortBookingRequest;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.wedriveu.services.vehicle.rabbitmq.Messages.VehicleStore.GET_VEHICLE_COMPLETED_NEAREST;
import static com.wedriveu.services.vehicle.rabbitmq.Messages.VehicleSubstitution.FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION;
import static com.wedriveu.services.vehicle.rabbitmq.Messages.VehicleSubstitution.SEND_SUBSTITUTION_VEHICLE_TO_USER;
import static com.wedriveu.shared.util.Constants.*;


/**
 * This Controller serves some interaction requests with the
 * {@linkplain com.wedriveu.services.vehicle.entity.VehicleStore} database. It handles the database replies
 * by deploying and interacting with other Verticles.
 *
 * @author Marco Baldassarri on 02/08/2017.
 * @author Nicola Lasagni
 */
public class NearestControl extends AbstractVerticle {

    private EventBus eventBus;

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.VehicleStore.AVAILABLE_COMPLETED, this::availableVehiclesCompleted);
        eventBus.consumer(Messages.VehicleFinder.VEHICLE_RESPONSE, this::handleVehicleResponses);
        eventBus.consumer(Messages.VehicleSubstitution.FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION_COMPLETED,
                this::handleVehicleResponsesForSubstitution);
        eventBus.consumer(Messages.VehicleFinder.NO_VEHICLE, this::handleNoVehicle);
        eventBus.consumer(Messages.VehicleSubstitution.NO_VEHICLE_FOR_SUBSTITUTION,
                this::handleNoVehicleForSubstitution);
        eventBus.consumer(Messages.VehicleStore.GET_AVAILABLE_VEHICLES_FOR_SUBSTITUTION_COMPLETED,
                this::availableVehiclesCompletedForSubstitution);
    }

    private void undeployFinder(String deploymentId) {
        if (deploymentId != null && !deploymentId.isEmpty()) {
            vertx.undeploy(deploymentId);
        }
    }

    private void handleNoVehicleForSubstitution(Message message) {
        handleNoVehicle(message, SEND_SUBSTITUTION_VEHICLE_TO_USER,
                com.wedriveu.services.vehicle.rabbitmq.Constants.NO_ELIGIBLE_VEHICLE_FOR_SUSTITUTION);
        JsonObject response = (JsonObject) message.body();
        AbortBookingRequest request = new AbortBookingRequest(response.getString(Constants.USERNAME));
        eventBus.send(Messages.Booking.ABORT_BOOKING, VertxJsonMapper.mapFrom(request));

    }

    private void handleNoVehicle(Message message) {
        handleNoVehicle(message, GET_VEHICLE_COMPLETED_NEAREST,
                com.wedriveu.services.vehicle.rabbitmq.Constants.NO_ELIGIBLE_VEHICLE);
    }

    private void handleNoVehicle(Message message, String address, String noVehicleMessage) {
        JsonObject response = (JsonObject) message.body();
        String deploymentId = response.getString(Constants.EventBus.DEPLOYMENT_ID);
        undeployFinder(deploymentId);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(USERNAME, response.getString(USERNAME));
        Vehicle noEligibleVehicle = new Vehicle();
        noEligibleVehicle.setNotEligibleVehicleFound(noVehicleMessage);
        jsonObject.put(VEHICLE, JsonObject.mapFrom(noEligibleVehicle).toString());
        vertx.eventBus().send(address, jsonObject);
    }

    private void handleVehicleResponsesForSubstitution(Message message) {
        handleVehicleResponses(message, true);
    }

    private void handleVehicleResponses(Message message) {
        handleVehicleResponses(message, false);
    }

    private void handleVehicleResponses(Message message,
                                        boolean forSubstitution) {
        JsonObject response = (JsonObject) message.body();
        String deploymentId = response.getString(Constants.EventBus.DEPLOYMENT_ID);
        undeployFinder(deploymentId);
        JsonArray responseListJson = response.getJsonArray(Messages.VehicleFinder.VEHICLE_RESPONSE_RESULT);
        handleEligibleVehicles(responseListJson, forSubstitution);
    }

    private void handleEligibleVehicles(JsonArray responseListJson, boolean forSubstitution) {
        List<VehicleResponseCanDrive> responseList = new ArrayList<>(responseListJson.size());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            VehicleResponseCanDrive[] vehicleResponses =
                    objectMapper.readValue(responseListJson.toString(), VehicleResponseCanDrive[].class);
            responseList = Arrays.asList(vehicleResponses);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendReplyToUser(getEligibleVehicles(responseList), forSubstitution);
    }

    private void sendReplyToUser(List<VehicleResponseCanDrive> eligibleVehicles,boolean forSubstitution) {
        String address = Messages.NearestControl.GET_VEHICLE_NEAREST;
        if (forSubstitution) {
            address = Messages.VehicleSubstitution.GET_NEAREST_VEHICLE_FOR_SUBSTITUTION;
        }
        vertx.eventBus().send(address, getBestVehicle(eligibleVehicles));
    }

    private List<VehicleResponseCanDrive> getEligibleVehicles(List<VehicleResponseCanDrive> responseList) {
        return responseList.stream().filter(VehicleResponseCanDrive::isEligible).collect(Collectors.toList());
    }

    private JsonObject getBestVehicle(List<VehicleResponseCanDrive> responseList) {
        responseList.sort(Comparator.comparing(v ->
                v.getDistanceToUser() / v.getVehicleSpeed()
        ));
        VehicleResponseCanDrive chosen = responseList.get(ZERO);
        return VertxJsonMapper.mapFrom(chosen);
    }

    private void availableVehiclesCompleted(Message message) {
        JsonObject userData = (JsonObject) message.body();
        deployFinderVerticle(Messages.NearestControl.DATA_TO_VEHICLE, userData);
    }

    private void availableVehiclesCompletedForSubstitution(Message message) {
        JsonObject userData = (JsonObject) message.body();
        deployFinderVerticle(FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION, userData);
    }

    private void deployFinderVerticle(String address, JsonObject data) {
        String id = UUID.randomUUID().toString();
        vertx.deployVerticle(new VehicleFinderVerticle(id), handler -> {
            if (handler.succeeded()) {
                this.eventBus.send(String.format(address, id), data);
            }
        });
    }

}


