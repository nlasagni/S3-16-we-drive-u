package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.entity.SubstitutionCheck;
import com.wedriveu.services.vehicle.entity.SubstitutionRequest;
import com.wedriveu.services.vehicle.entity.UserRequest;
import com.wedriveu.services.vehicle.entity.VehicleResponseCanDrive;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.CanDriveRequest;
import com.wedriveu.shared.rabbitmq.message.CanDriveResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.shared.util.Position;
import com.wedriveu.shared.util.PositionUtils;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.EVENT_BUS_FINDER_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_FINDER;
import static com.wedriveu.services.vehicle.rabbitmq.Messages.VehicleSubstitution.FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION;
import static com.wedriveu.shared.util.Constants.USERNAME;
import static com.wedriveu.shared.util.Constants.ZERO;

/**
 * Handles the communication with all the current available vehicles.
 * The available vehicle set has been chosen thanks to the client information data; this Verticle asks for the
 * actual availability of the vehicles by using RabbitMQ Vert.x library.
 * At the end of the process, only one vehicle is being picked up.
 *
 * @author Marco Baldassarri on 5/08/2017.
 * @author Nicola Lasagni
 */
public class VehicleFinderVerticle extends VerticleConsumer {

    private static final long TIME_OUT = 15000;

    private String id;
    private boolean isForSubstitution;
    private String username;
    private List<Vehicle> availableVehicles;
    private Position userPosition;
    private Position destPosition;
    private int counter;
    private double distanceToUser;
    private double totalDistance;
    private long timerId;
    private JsonArray responseJsonArray;
    private VehicleResponseCanDrive vehicleResponseCanDrive;

    public VehicleFinderVerticle(String id) {
        super(String.format(VEHICLE_SERVICE_QUEUE_FINDER, id));
        this.id = id;
    }

    @Override
    public void start() throws Exception {
        super.start();
        eventBus.consumer(String.format(Messages.NearestControl.DATA_TO_VEHICLE, id), this::sendDataToVehicle);
        eventBus.consumer(String.format(FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION, id), this::handleSubstitution);
    }

    private void handleSubstitution(Message message) {
        JsonObject body = (JsonObject) message.body();
        SubstitutionRequest substitutionRequest =
                VertxJsonMapper.mapTo(body, SubstitutionRequest.class);
        isForSubstitution = true;
        availableVehicles = substitutionRequest.getVehicleList();
        SubstitutionCheck substitutionCheck = substitutionRequest.getSubstitutionCheck();
        userPosition = substitutionCheck.getSourcePosition();
        destPosition = substitutionCheck.getDestinationPosition();
        username = substitutionCheck.getVehicleUpdate().getUsername();
        responseJsonArray = new JsonArray();
        startFindProcess();
    }

    private void sendDataToVehicle(Message message) {
        JsonObject body = (JsonObject) message.body();
        UserRequest userRequest = body.mapTo(UserRequest.class);
        availableVehicles = userRequest.getVehicleList();
        userPosition = userRequest.getUserPosition();
        destPosition = userRequest.getDestinationPosition();
        username = userRequest.getUsername();
        responseJsonArray = new JsonArray();
        startFindProcess();
    }

    private void startFindProcess() {
        if (availableVehicles == null || availableVehicles.isEmpty()) {
            sendNoVehicleFound();
        } else {
            Future<Void> future = Future.future();
            future.setHandler(handler -> {
                startVehicleCommunication();
            });
            try {
                startFinderConsumer(future);
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private void startFinderConsumer(Future<Void> future) throws IOException, TimeoutException {
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.CAN_DRIVE_RESPONSE, username),
                EVENT_BUS_FINDER_ADDRESS + id,
                future);
    }

    private void sendNoVehicleFound() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.DEPLOYMENT_ID, deploymentID());
        jsonObject.put(USERNAME, username);
        if (client != null) {
            client.stop(null);
        }
        String address = Messages.VehicleFinder.NO_VEHICLE;
        if (isForSubstitution) {
            address = Messages.VehicleSubstitution.NO_VEHICLE_FOR_SUBSTITUTION;
        }
        eventBus.send(address, jsonObject);
    }

    private void sendResponse(JsonArray vehicles) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.DEPLOYMENT_ID, deploymentID());
        jsonObject.put(USERNAME, username);
        jsonObject.put(Messages.VehicleFinder.VEHICLE_RESPONSE_RESULT, vehicles);
        if (client != null) {
            client.stop(null);
        }
        String address = Messages.VehicleFinder.VEHICLE_RESPONSE;
        if (isForSubstitution) {
            address = Messages.VehicleSubstitution.FIND_NEAREST_VEHICLE_FOR_SUBSTITUTION_COMPLETED;
        }
        eventBus.send(address, jsonObject);
    }

    private void startVehicleCommunication() {
        client = RabbitMQClientFactory.createClient(vertx);
        client.start(onStartCompleted -> {
            if (onStartCompleted.succeeded()) {
                publishToMultipleRoutingKeys();
            }
        });
    }

    private void publishToMultipleRoutingKeys() {
        IntStream.range(ZERO, availableVehicles.size()).forEach(index -> {
            publishToConsumer(Constants.RabbitMQ.Exchanges.VEHICLE,
                    String.format(Constants.RabbitMQ.RoutingKey.CAN_DRIVE_REQUEST,
                            availableVehicles.get(index).getLicensePlate()), index);
        });
        startResponseTimeout();
    }

    private void startResponseTimeout() {
        timerId = vertx.setTimer(TIME_OUT, onTimeOut -> {
            client.stop(onStop -> {
                if (counter == 0) {
                    sendNoVehicleFound();
                } else {
                    sendResponse(responseJsonArray);
                }
            });
        });
    }

    private void publishToConsumer(String exchangeName,
                                   String routingKey,
                                   int index) {
        JsonObject requestJson = getRequestObject(index);
        client.basicPublish(exchangeName, routingKey, requestJson, onPublish -> {
            if (!onPublish.succeeded()) {
                Log.error(this.getClass().getSimpleName(),
                        onPublish.cause().getLocalizedMessage(),
                        onPublish.cause());
            }
        });
    }

    private JsonObject getRequestObject(int index) {
        Position vehiclePosition = availableVehicles.get(index).getPosition();
        distanceToUser = PositionUtils.getDistanceInKm(userPosition, vehiclePosition);
        totalDistance = (distanceToUser) + (PositionUtils.getDistanceInKm(userPosition, destPosition));
        CanDriveRequest canDriveRequest = new CanDriveRequest();
        canDriveRequest.setUsername(username);
        canDriveRequest.setDistanceInKm(totalDistance);
        return VertxJsonMapper.mapInBodyFrom(canDriveRequest);
    }

    @Override
    public void registerConsumer(String eventBusAddress) {
        eventBus.consumer(eventBusAddress, msg -> {
            JsonObject responseJson = (JsonObject) msg.body();
            String response = responseJson.getString(Constants.EventBus.BODY);
            CanDriveResponse canDriveResponse = (new JsonObject(response)).mapTo(CanDriveResponse.class);
            vehicleResponseCanDrive = new VehicleResponseCanDrive();
            vehicleResponseCanDrive.setEligible(canDriveResponse.getOk());
            vehicleResponseCanDrive.setLicensePlate(canDriveResponse.getLicense());
            vehicleResponseCanDrive.setVehicleSpeed(canDriveResponse.getSpeed());
            vehicleResponseCanDrive.setUsername(username);
            vehicleResponseCanDrive.setDistanceToUser(distanceToUser);
            vehicleResponseCanDrive.setTotalDistance(totalDistance);
            responseJsonArray.add(JsonObject.mapFrom(vehicleResponseCanDrive));
            counter++;
            if (counter == availableVehicles.size()) {
                vertx.cancelTimer(timerId);
                sendResponse(responseJsonArray);
            }
        });
    }

}

