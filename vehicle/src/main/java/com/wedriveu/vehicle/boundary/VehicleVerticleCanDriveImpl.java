package com.wedriveu.vehicle.boundary;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.shared.rabbitmq.message.CanDriveRequest;
import com.wedriveu.shared.rabbitmq.message.CanDriveResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.vehicle.control.CanDriveChecker;
import com.wedriveu.vehicle.control.CanDriveCheckerImpl;
import com.wedriveu.vehicle.control.VehicleControl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;

/**
 * @author Michele Donati on 09/08/2017.
 */

public class VehicleVerticleCanDriveImpl extends AbstractVerticle implements VehicleVerticleCanDrive {

    private VehicleControl vehicle;
    private static final String TAG = VehicleVerticleCanDriveImpl.class.getSimpleName();
    private static final String EVENT_BUS_ADDRESS = "vehicle.candrive";
    private static final String READ_ERROR = "Error occurred while reading request.";
    private static final String SEND_ERROR = "Error occurred while sending response.";
    private static final String ENGINE_ILLEGAL_STATE = "The Engine has not been started yet or it has been stopped.";
    private String queue;

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;
    private ObjectMapper objectMapper;
    private CanDriveChecker checker;

    public VehicleVerticleCanDriveImpl(VehicleControl vehicle) {
        this.vehicle = vehicle;
        queue = "vehicle.candrive." + this.vehicle.getVehicle().plate();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        eventBus = vertx.eventBus();
        checker = new CanDriveCheckerImpl(vehicle.getVehicle());
        objectMapper = new ObjectMapper();
        startService(startFuture);
    }

    private void startService(Future<Void> future) {
        JsonObject config = new JsonObject();
        config.put(Constants.RabbitMQ.ConfigKey.HOST, Constants.RabbitMQ.Broker.HOST);
        config.put(Constants.RabbitMQ.ConfigKey.PASSWORD, Constants.RabbitMQ.Broker.PASSWORD);
        config.put(Constants.RabbitMQ.ConfigKey.PORT, Constants.RabbitMQ.Broker.PORT);
        rabbitMQClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
        Future<Void> initFuture = Future.future();
        Future<Void> endFuture = Future.future();
        startClient(initFuture);
        initFuture.compose(v -> {
            Future<JsonObject> declareQueueFuture = Future.future();
            declareQueue(declareQueueFuture);
            return declareQueueFuture;
        }).compose(v -> {
            Future<Void> bindQueueFuture = Future.future();
            bindQueueToExchange(bindQueueFuture);
            return bindQueueFuture;
        }).compose(v -> {
            Future<Void> basicConsumeFuture = Future.future();
            basicConsume(basicConsumeFuture);
            return basicConsumeFuture;
        }).compose(v -> {
            registerConsumer();
            future.complete();
        }, endFuture);
    }

    private void startClient(Future<Void> future) {
        rabbitMQClient.start(future.completer());
    }

    private void declareQueue(Future<JsonObject> future) {
        rabbitMQClient.queueDeclare(queue,
                true,
                false,
                false,
                future.completer());
    }

    private void bindQueueToExchange(Future<Void> future) {
        rabbitMQClient.queueBind(queue,
                Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.CAN_DRIVE_REQUEST, vehicle.getVehicle().plate()),
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(queue, EVENT_BUS_ADDRESS, future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            CanDriveResponse response = null;
            try {
                JsonObject message = new JsonObject(msg.body().toString());
                CanDriveRequest canDriveRequest =
                        objectMapper.readValue(message.getString(Constants.EventBus.BODY), CanDriveRequest.class);
                response = canDrive(canDriveRequest);
            } catch (IOException e) {
                Log.error(TAG, READ_ERROR, e);
            }
            sendResponse(response);
        });
    }

    private void sendResponse(CanDriveResponse response) {
        try {
            String responseString = objectMapper.writeValueAsString(response);
            JsonObject responseJson = new JsonObject();
            responseJson.put(Constants.EventBus.BODY, responseString);
            rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                    String.format(Constants.RabbitMQ.RoutingKey.CAN_DRIVE_RESPONSE, vehicle.getUsername()),
                    responseJson,
                    onPublish -> {
                        if (!onPublish.succeeded()) {
                            Log.error(TAG, SEND_ERROR, onPublish.cause());
                        }
                    });
        } catch (JsonProcessingException e) {
            Log.error(TAG, SEND_ERROR, e);
        }
    }

    @Override
    public CanDriveResponse canDrive(CanDriveRequest canDriveRequest) throws IllegalStateException {
        if (checkIllegalState()) {
            throw new IllegalStateException(ENGINE_ILLEGAL_STATE);
        }
        Double distance = canDriveRequest.getDistanceInKm();
        vehicle.setUsername(canDriveRequest.getUsername());
        CanDriveResponse response = new CanDriveResponse();
        response.setLicense(vehicle.getVehicle().plate());
        response.setOk(checker.checkJourney(distance));
        response.setSpeed(vehicle.getVehicle().speed());
        return response;
    }

    private boolean checkIllegalState() {
        return context == null || deploymentID().isEmpty();
    }

}
