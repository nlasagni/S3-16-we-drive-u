package com.wedriveu.services.vehicle.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.rabbitmq.nearest.VehicleResponse;
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

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * This Controller serves all the interaction requests with the
 * {@linkplain com.wedriveu.services.vehicle.entity.VehicleStore} database. It handles the database replies
 * by deploying and interacting with other Verticles.
 *
 * @author Marco Baldassarri on 02/08/2017.
 */
public class NearestControl extends AbstractVerticle {

    private EventBus eventBus;
    private List<VehicleResponse> responseList;

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.VehicleStore.AVAILABLE_COMPLETED, this::availableVehiclesCompleted);
        eventBus.consumer(Messages.VehicleFinder.VEHICLE_RESPONSE, this::handleVehicleResponses);
    }

    private void handleVehicleResponses(Message message) {
        JsonArray eligibleVehicles = (JsonArray) message.body();
        responseList = new ArrayList<>(eligibleVehicles.size());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            VehicleResponse[] vehicleResponses =
                    objectMapper.readValue(eligibleVehicles.toString(), VehicleResponse[].class);
            responseList = Arrays.asList(vehicleResponses);
        } catch (IOException e) {
            e.printStackTrace();
        }
        vertx.eventBus().send(Messages.NearestControl.GET_VEHICLE, getBestVehicle(responseList));
    }

    private JsonObject getBestVehicle(List<VehicleResponse> responseList) {
        responseList.sort(Comparator.comparing(v ->
                v.getDistanceToUser() / v.getVehicleSpeed()
        ));
        VehicleResponse chosen = responseList.get(ZERO);
        JsonObject bestVehicleJson = new JsonObject();
        bestVehicleJson.put(USERNAME, chosen.getUsername());
        bestVehicleJson.put(CAR_LICENCE_PLATE, chosen.getLicencePlate());
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


