package com.wedriveu.vehicle.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.shared.rabbitmq.message.RegisterToServiceResponse;
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
import java.util.Date;
import java.util.UUID;

/**
 * @author Michele Donati on 10/08/2017.
 */

public class VehicleVerticleRegisterImpl extends AbstractVerticle implements VehicleVerticleRegister {

    private VehicleControl vehicle;
    private static final String TAG = VehicleVerticleRegisterImpl.class.getSimpleName();
    private static final String EVENT_BUS_ADDRESS = "vehicle.register";
    private static final String READ_ERROR = "Error occurred while reading response.";
    private String queue;

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;
    private ObjectMapper objectMapper;

    private VehicleConstants$ vehicleConstants = VehicleConstants$.MODULE$;

    public VehicleVerticleRegisterImpl(VehicleControl vehicle) {
        this.vehicle = vehicle;
        this.queue = "vehicle.register." + this.vehicle.getVehicle().plate();
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
            registerToService(vehicle.getVehicle().plate());
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
                String.format(Constants.RabbitMQ.RoutingKey.REGISTER_RESPONSE, vehicle.getVehicle().plate()),
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(queue, EVENT_BUS_ADDRESS, future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            try {
                JsonObject message = new JsonObject(msg.body().toString());
                RegisterToServiceResponse registerToServiceResponse =
                        objectMapper.readValue(message.getString(Constants.EventBus.BODY),
                                RegisterToServiceResponse.class);
                checkResponse(registerToServiceResponse);
            } catch (IOException e) {
                Log.error(TAG, READ_ERROR, e);
            }
        });
    }

    @Override
    public void registerToService(String license) {
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.REGISTER_REQUEST,
                createRequest(),
                onPublish -> {
                    onPublish.succeeded();
                });
    }

    private JsonObject createRequest() {
        Vehicle vehicleTest = new Vehicle();
        vehicleTest.setDescription(vehicle.getVehicle().description());
        vehicleTest.setImageUrl(vehicle.getVehicle().imageUrl());
        vehicleTest.setLicensePlate(vehicle.getVehicle().plate());
        vehicleTest.setStatus(vehicle.getVehicle().getState());
        vehicleTest.setPosition(vehicle.getVehicle().getPosition());
        vehicleTest.setLastUpdate(new Date());
        vehicleTest.setName("");

        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(vehicleTest).toString());
        return jsonObject;
    }

    private void checkResponse(RegisterToServiceResponse response) {
        if (!response.getRegisterOk()) {
            String newLicensePlate = calculateNewLicensePlate(vehicle);
            registerToService(newLicensePlate);
        }
        vehicle.getVehicle().setState(Constants.Vehicle.STATUS_AVAILABLE);
        eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicle.getVehicle().plate()),
                new JsonObject());
    }

    private String calculateNewLicensePlate(VehicleControl vehicle) {
        String newLicense = UUID.randomUUID().toString();
        if (vehicle.getVehicle().plate().equals(newLicense)) {
            return calculateNewLicensePlate(vehicle);
        }
        vehicle.getVehicle().setPlate(newLicense);
        return newLicense;
    }

}
