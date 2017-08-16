package com.wedriveu.services.vehicle.boundary.nearest;


import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.services.shared.message.VehicleResponseCanDrive;
import com.wedriveu.services.shared.util.PositionUtils;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.services.vehicle.rabbitmq.UserRequest;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.EVENT_BUS_FINDER_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_FINDER;
import static com.wedriveu.shared.util.Constants.USERNAME;
import static com.wedriveu.shared.util.Constants.ZERO;

/**
 * Handles the communication with all the current available vehicles.
 * The available vehicle set has been chosen thanks to the client information data; this Verticle asks for the
 * actual availability of the vehicles by using RabbitMQ Vert.x library.
 * At the end of the process, only one vehicle is being picked up.
 *
 * @author Marco Baldassarri on 5/08/2017.
 */
public class VehicleFinderVerticle extends VerticleConsumer {

    private static final long TIME_OUT = 30000;

    private static RabbitMQClient client;
    private String username;
    private List<Vehicle> availableVehicles;
    private Position userPosition;
    private Position destPosition;
    private int counter;
    private double distanceToUser;
    private UserRequest userRequest;
    private JsonArray responseJsonArray;
    private VehicleResponseCanDrive vehicleResponseCanDrive;

    public VehicleFinderVerticle(String id) {
        super(String.format(VEHICLE_SERVICE_QUEUE_FINDER, id));
    }

    @Override
    public void start() throws Exception {
        super.start();
        eventBus.consumer(Messages.NearestControl.DATA_TO_VEHICLE, this::sendDataToVehicle);
    }

    private void sendDataToVehicle(Message message) {
        prepareData((JsonObject) message.body());
        if (availableVehicles == null || availableVehicles.isEmpty()) {
            sendNoVehicleFound();
        } else {
            startVehicleCommunication();
            try {
                startFinderConsumer();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private void prepareData(JsonObject body) {
        userRequest = body.mapTo(UserRequest.class);
        availableVehicles = userRequest.getVehicleList();
        userPosition = userRequest.getUserPosition();
        destPosition = userRequest.getDestinationPosition();
        username = userRequest.getUsername();
        responseJsonArray = new JsonArray();
    }

    private void sendNoVehicleFound() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.DEPLOYMENT_ID, deploymentID());
        jsonObject.put(USERNAME, username);
        eventBus.send(Messages.VehicleFinder.NO_VEHICLE, jsonObject);
    }

    private void sendResponse(JsonArray vehicles) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.DEPLOYMENT_ID, deploymentID());
        jsonObject.put(USERNAME, username);
        jsonObject.put(Messages.VehicleFinder.VEHICLE_RESPONSE_RESULT, vehicles);
        eventBus.send(Messages.VehicleFinder.VEHICLE_RESPONSE, jsonObject);
    }

    private void startVehicleCommunication() {
        client = RabbitMQClientFactory.createClient(vertx);
        client.start(onStartCompleted -> {
                    if (onStartCompleted.succeeded()) {
                        declareExchanges(onDeclareCompleted -> {
                            if (onDeclareCompleted.succeeded()) {
                                publishToMultipleRoutingKeys();
                            }
                        });
                    }
                }
        );
    }

    private void publishToMultipleRoutingKeys() {
        IntStream.range(ZERO, availableVehicles.size()).forEach(index -> {
            publishToConsumer(Constants.RabbitMQ.Exchanges.VEHICLE,
                    String.format(Constants.RabbitMQ.RoutingKey.CAN_DRIVE_REQUEST,
                            availableVehicles.get(index).getLicensePlate()), index);
        });
    }

    private void declareExchanges(Handler<AsyncResult<Void>> handler) {
        client.exchangeDeclare(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.Exchanges.Type.DIRECT,
                false,
                false,
                handler);
    }

    private void publishToConsumer(String exchangeName,
                                   String routingKey,
                                   int index) {
        JsonObject requestJson = new JsonObject();
        requestJson.put(Constants.EventBus.BODY, getRequestObject(index).encode());
        client.basicPublish(exchangeName, routingKey, requestJson, null);
    }

    private JsonObject getRequestObject(int index) {
        Position vehiclePosition = availableVehicles.get(index).getPosition();
        distanceToUser = PositionUtils.getDistanceInKm(userPosition, vehiclePosition);
        double tripDistance = (distanceToUser) + (PositionUtils.getDistanceInKm(userPosition, destPosition));
        userRequest.setTripDistance(tripDistance);
        return JsonObject.mapFrom(userRequest);
    }


    private void startFinderConsumer() throws IOException, TimeoutException {
        startConsumer(false,
                Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.CAN_DRIVE_RESPONSE, username),
                EVENT_BUS_FINDER_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBusAddress) {
        vertx.setTimer(TIME_OUT, onTimeOut -> client.stop(onStop -> sendNoVehicleFound()));
        eventBus.consumer(eventBusAddress, msg -> {
            counter++;
            if (counter <= availableVehicles.size()) {
                JsonObject responseJson = (JsonObject) msg.body();
                String response = responseJson.getString(Constants.EventBus.BODY);
                vehicleResponseCanDrive = (new JsonObject(response)).mapTo(VehicleResponseCanDrive.class);
                vehicleResponseCanDrive.setUsername(username);
                vehicleResponseCanDrive.setDistanceToUser(distanceToUser);
                responseJsonArray.add(JsonObject.mapFrom(vehicleResponseCanDrive));
            } else {
                sendResponse(responseJsonArray);
            }
        });
    }

}

