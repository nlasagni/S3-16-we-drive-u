package com.wedriveu.vehicle.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.shared.util.Position;
import com.wedriveu.vehicle.control.VehicleControl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;

/**
 * @author Michele Donati on 17/08/2017.
 */

public class VehicleVerticleForUserImpl extends AbstractVerticle implements VehicleVerticleForUser {

    private VehicleControl vehicle;
    private static final String TAG = VehicleVerticleForUserImpl.class.getSimpleName();
    private static final String EVENT_BUS_ADDRESS = "vehicle.enter";
    private static final String READ_ERROR = "Error occurred while reading response.";
    private static String queue;

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;
    private ObjectMapper objectMapper;
    private Position destinationPosition;

    public VehicleVerticleForUserImpl(VehicleControl vehicle) {
        this.vehicle = vehicle;
        queue = "vehicle.enter." + this.vehicle.getVehicle().plate();
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
        rabbitMQClient.queueDeclare(queue,
                true,
                false,
                false,
                future.completer());
    }

    private void bindQueueToExchange(Future<Void> future) {
        rabbitMQClient.queueBind(queue,
                Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE_ENTER_USER,
                        vehicle.getVehicle().getPlate()),
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(queue, EVENT_BUS_ADDRESS, future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            vehicle.setUserOnBoard(true);
            vehicle.goToDestination(destinationPosition);
        });

        eventBus.consumer(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_FOR_USER, vehicle.getVehicle().plate()),
                message -> {
                    JsonObject msg = new JsonObject(message.body().toString());
                    Position destPosition = null;
                    try {
                        destPosition =
                                objectMapper.readValue(msg.getString(Constants.EventBus.BODY), Position.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    enterInVehicle(destPosition);
                });
    }

    @Override
    public void enterInVehicle(Position destinationPosition) {
        this.destinationPosition = destinationPosition;
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST_ENTER_USER, vehicle.getUsername()),
                createRequest(),
                onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.info(this.getClass().getSimpleName(), onPublish.cause().getLocalizedMessage());
                    }
                });
    }

    private JsonObject createRequest() {
        EnterVehicleRequest request = new EnterVehicleRequest();
        request.setLicensePlate(vehicle.getVehicle().plate());
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(request).toString());
        return jsonObject;
    }

}
