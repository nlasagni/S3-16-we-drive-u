package com.weriveu.vehicle.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.shared.rabbitmq.message.Vehicle;
import com.wedriveu.shared.rabbitmq.message.VehicleBookRequest;
import com.wedriveu.shared.rabbitmq.message.VehicleBookResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.shared.VehicleConstants$;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;

/**
 * @author Michele Donati on 11/08/2017.
 */

public class VehicleVerticleBookImpl extends AbstractVerticle implements VehicleVerticleBook {

    private VehicleControl vehicle;
    private static final String TAG = VehicleVerticleBookImpl.class.getSimpleName();
    private static final String EVENT_BUS_ADDRESS = "vehicle.book";
    private static final String READ_ERROR = "Error occurred while reading request.";
    private static final String SEND_ERROR = "Error occurred while sending response.";
    private static final String ENGINE_ILLEGAL_STATE = "The Engine has not been started yet or it has been stopped.";
    private static String QUEUE_NAME = "vehicle.book.";

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;
    private ObjectMapper objectMapper;

    public VehicleVerticleBookImpl(VehicleControl vehicle) {
        this.vehicle = vehicle;
        QUEUE_NAME+=this.vehicle.getVehicle().plate();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        eventBus = vertx.eventBus();
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
        rabbitMQClient.queueDeclare(QUEUE_NAME,
                true,
                false,
                false,
                future.completer());
    }

    private void bindQueueToExchange(Future<Void> future) {
        rabbitMQClient.queueBind(QUEUE_NAME,
                Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.BOOK_REQUEST, vehicle.getVehicle().plate()),
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(QUEUE_NAME, EVENT_BUS_ADDRESS, future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            VehicleBookResponse response = null;
            try {
                JsonObject message = new JsonObject(msg.body().toString());
                VehicleBookRequest bookRequest =
                        objectMapper.readValue(message.getString(Constants.EventBus.BODY), VehicleBookRequest.class);
                response = book(bookRequest);
            } catch (IOException e) {
                Log.error(TAG, READ_ERROR, e);
            }
            sendResponse(response);
        });
    }

    private void sendResponse(VehicleBookResponse response){
        try {
            String responseString = objectMapper.writeValueAsString(response);
            JsonObject responseJson = new JsonObject();
            responseJson.put(Constants.EventBus.BODY, responseString);
            rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                    String.format(Constants.RabbitMQ.RoutingKey.BOOK_RESPONSE, vehicle.getVehicle().plate()),
                    responseJson,
                    onPublish -> {
                        if (!onPublish.succeeded()) {
                            Log.error(TAG, SEND_ERROR, onPublish.cause());
                        }
                        vehicle.getVehicle().setState(VehicleConstants$.MODULE$.stateBooked());
                    });
        }catch (JsonProcessingException e) {
            Log.error(TAG, SEND_ERROR, e);
        }
    }

    @Override
    public VehicleBookResponse book(VehicleBookRequest request) throws IllegalStateException {
        if (checkIllegalState()) {
            throw new IllegalStateException(ENGINE_ILLEGAL_STATE);
        }
        vehicle.setUsername(request.getUsername());
        VehicleBookResponse response = new VehicleBookResponse();
        response.setBooked(vehicle.getVehicle().getState().equals(Vehicle.STATUS_AVAILABLE));
        response.setSpeed(vehicle.getVehicle().speed());
        return response;
    }

    private boolean checkIllegalState() {
        return context == null || deploymentID().isEmpty();
    }

}
