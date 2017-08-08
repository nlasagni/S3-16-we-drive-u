package com.wedriveu.services.vehicle.boundary.nearest;

import com.wedriveu.services.shared.rabbitmq.RabbitMQConfig;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.rabbitmq.nearest.VehicleResponse;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.shared.utilities.PositionUtils;
import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.services.vehicle.rabbitmq.UserRequest;
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

import static com.wedriveu.services.shared.utilities.Constants.*;

/**
 * Handles the communication with all the current available vehicles.
 * The available vehicle set has been chosen thanks to the client information data; this Verticle asks for the
 * actual availability of the vehicles by using RabbitMQ Vert.x library.
 * At the end of the process, only one vehicle is being picked up.
 *
 * @author Marco Baldassarri
 * @since 5/08/2017
 */
public class VehicleFinderVerticle extends VerticleConsumer {

    private static RabbitMQClient client;
    private String username;
    private List<Vehicle> availableVehicles;
    private Position userPosition;
    private Position destPosition;
    private int counter;
    private double distanceToUser;
    private UserRequest userRequest;
    private JsonArray responseJsonArray;

    public VehicleFinderVerticle() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);
    }

    @Override
    public void start() throws Exception {
        super.start();
        eventBus.consumer(Messages.NearestControl.DATA_TO_VEHICLE, this::sendDataToVehicle);
    }

    private void sendDataToVehicle(Message message) {
        prepareData((JsonObject) message.body());
        startVehicleCommunication();
        try {
            startFinderConsumer();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
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

    private void startVehicleCommunication() {
        client = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
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
            publishToConsumer(Constants.VEHICLE_SERVICE_EXCHANGE,
                    String.format(Constants.ROUTING_KEY_CAN_DRIVE,
                            availableVehicles.get(index).getCarLicencePlate()), index);
        });
    }

    private void declareExchanges(Handler<AsyncResult<Void>> handler) {
        client.exchangeDeclare(Constants.VEHICLE_SERVICE_EXCHANGE,
                EXCHANGE_TYPE,
                false,
                false,
                handler);
    }

    private void publishToConsumer(String exchangeName,
                                   String routingKey,
                                   int index) {
        JsonObject requestJson = new JsonObject();
        requestJson.put(BODY, getRequestObject(index).encode());
        client.basicPublish(exchangeName, routingKey, requestJson, null);
    }

    private JsonObject getRequestObject(int index) {
        Position vehiclePosition = availableVehicles.get(index).getPosition();
        distanceToUser = PositionUtils.getDistanceInKm(userPosition, vehiclePosition);
        double tripDistance = (distanceToUser) + (PositionUtils.getDistanceInKm(userPosition, destPosition));
        userRequest.setTripDistance(tripDistance);
        return new JsonObject().mapFrom(userRequest);
    }


    private void startFinderConsumer() throws IOException, TimeoutException {
        startConsumer(VEHICLE_SERVICE_EXCHANGE,
                String.format(ROUTING_KEY_CAN_DRIVE_RESPONSE, username),
                EVENT_BUS_FINDER_ADDRESS);
    }

    @Override
    public void registerConsumer(String eventBus) {
        counter++;
        if (counter <= availableVehicles.size()) {
            vertx.eventBus().consumer(eventBus, msg -> {
                JsonObject responseJson = (JsonObject) msg.body();
                String response = responseJson.getString(BODY);
                VehicleResponse vehicleResponse = (new JsonObject(response)).mapTo(VehicleResponse.class);
                if (vehicleResponse.isEligible()) {
                    vehicleResponse.setUsername(username);
                    vehicleResponse.setDistanceToUser(distanceToUser);
                    responseJsonArray.add(new JsonObject().mapFrom(vehicleResponse));
                    if (counter == availableVehicles.size()) {
                        vertx.eventBus().send(Messages.VehicleFinder.VEHICLE_RESPONSE, responseJsonArray);
                        vertx.undeploy(deploymentID());
                    }
                }
            });

        }
    }

}

