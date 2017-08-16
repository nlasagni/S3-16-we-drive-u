package com.weriveu.vehicle.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.shared.rabbitmq.message.Vehicle;
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
import java.net.MalformedURLException;
import java.net.URL;
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
    private static String QUEUE_NAME = "vehicle.register.";

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;
    private ObjectMapper objectMapper;

    private VehicleConstants$ vehicleConstants = VehicleConstants$.MODULE$;

    public VehicleVerticleRegisterImpl(VehicleControl vehicle) {
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
            registerToService(vehicle.getVehicle().plate());
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
                String.format(Constants.RabbitMQ.RoutingKey.REGISTER_RESPONSE, vehicle.getVehicle().plate()),
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(QUEUE_NAME, EVENT_BUS_ADDRESS, future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            try {
                System.out.println("VERTICLE = CONSUMO IL MESSAGGIO!");
                JsonObject message = new JsonObject(msg.body().toString());
                RegisterToServiceResponse registerToServiceResponse =
                        objectMapper.readValue(message.getString(Constants.EventBus.BODY),
                                RegisterToServiceResponse.class);
                System.out.println("VERTICLE = IL MESSAGGIO è " + registerToServiceResponse.toString());
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
            if(onPublish.succeeded()){
                Log.info("VERTICLE","HO MANDATO IL MESSAGGIO");
            }
        });
    }

    private JsonObject createRequest() {
    //    RegisterToServiceRequest request = new RegisterToServiceRequest();
    //    request.setLicense(vehicle.getVehicle().plate());
        Vehicle vehicleTest = new Vehicle();
        vehicleTest.setDescription(vehicle.getVehicle().description());
        try {
            vehicleTest.setImageUrl(new URL(vehicle.getVehicle().imageUrl()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        vehicleTest.setLicensePlate(vehicle.getVehicle().plate());
        vehicleTest.setStatus(vehicle.getVehicle().getState());
        vehicleTest.setPosition(vehicle.getVehicle().getPosition());
        vehicleTest.setLastUpdate(new Date());
        vehicleTest.setName("Super Car");

        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(vehicleTest).toString());
        return jsonObject;
    }

    private void checkResponse(RegisterToServiceResponse response) {
        if(!response.getRegisterOk()){
            String newLicensePlate = calculateNewLicensePlate(vehicle);
            registerToService(newLicensePlate);
        }
        System.out.println("MI SONO REGISTRATO");
        vehicle.getVehicle().setState(vehicleConstants.stateAvailable());
    }

    private String calculateNewLicensePlate(VehicleControl vehicle) {
        String newLicense = UUID.randomUUID().toString();
        if(vehicle.getVehicle().plate().equals(newLicense)) {
            return calculateNewLicensePlate(vehicle);
        }
        vehicle.getVehicle().setPlate(newLicense);
        return newLicense;
    }

}
