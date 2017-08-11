package com.weriveu.vehicle.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.rabbitmq.RabbitMQConfig;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.shared.entity.RegisterToServiceRequest;
import com.wedriveu.shared.entity.RegisterToServiceResponse;
import com.wedriveu.shared.utils.Log;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.shared.EventBusConstants$;
import com.wedriveu.vehicle.shared.Exchanges$;
import com.wedriveu.vehicle.shared.RoutingKeys$;
import com.wedriveu.vehicle.shared.VehicleConstants$;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * @author Michele Donati on 10/08/2017.
 */

public class VehicleVerticleRegisterImpl extends AbstractVerticle implements VehicleVerticleRegister {

    private VehicleControl vehicle;
    private static final String TAG = VehicleVerticleRegisterImpl.class.getSimpleName();
    private static final String EVENT_BUS_ADDRESS = "vehicle.register";
    private static final String READ_ERROR = "Error occurred while reading response.";
    private static final String SEND_ERROR = "Error occurred while sending request.";
    private static final String ENGINE_ILLEGAL_STATE = "The Engine has not been started yet or it has been stopped.";
    private static String QUEUE_NAME = "vehicle.";

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;
    private ObjectMapper objectMapper;

    private VehicleConstants$ vehicleConstants = VehicleConstants$.MODULE$;
    private Exchanges$ exchanges = Exchanges$.MODULE$;
    private RoutingKeys$ routingKeys = RoutingKeys$.MODULE$;
    private EventBusConstants$ eventBusConstants = EventBusConstants$.MODULE$;

    public VehicleVerticleRegisterImpl(VehicleControl vehicle) {
        this.vehicle = vehicle;
        System.out.println("LA TARGA DEL VEIOCOLO = " + vehicle.getVehicle().plate());
        QUEUE_NAME+=this.vehicle.getVehicle().plate();
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        eventBus = vertx.eventBus();
        objectMapper = new ObjectMapper();
        startService(startFuture);
        System.out.println("MI SONO DEPLOYATO");
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
            System.out.println("Basic Consume " + QUEUE_NAME + " " + exchanges.VEHICLE() + " " +  String.format(routingKeys.REGISTER_RESPONSE(), vehicle.getVehicle().plate()));
            return basicConsumeFuture;
        }).compose(v -> {
            System.out.println("Register Consumer " + QUEUE_NAME + " " + exchanges.VEHICLE() + " " + String.format(routingKeys.REGISTER_RESPONSE(), vehicle.getVehicle().plate()));
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
                exchanges.VEHICLE(),
                String.format(routingKeys.REGISTER_RESPONSE(), vehicle.getVehicle().plate()),
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(QUEUE_NAME, EVENT_BUS_ADDRESS, future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            try {
                System.out.println("HO PRESO IL MESSAGGIO");
                JsonObject message = new JsonObject(msg.body().toString());
                System.out.println("il messaggio è == " + message);
                RegisterToServiceResponse registerToServiceResponse =
                        objectMapper.readValue(message.getString(eventBusConstants.BODY()),
                                RegisterToServiceResponse.class);
                System.out.println("LA RISPOSTA DEL SERVICE è = " + registerToServiceResponse.getRegisterOk());
                checkResponse(registerToServiceResponse);
            } catch (IOException e) {
                Log.error(TAG, READ_ERROR, e);
            }
        });
    }

    @Override
    public void registerToService(String license) {
        rabbitMQClient.basicPublish(exchanges.VEHICLE(), routingKeys.REGISTER_REQUEST(), createRequest(), onPublish -> {
            if(onPublish.succeeded()){
                System.out.println("HO MANDATO LA RICHIESTA");
            }
        });
    }

    private JsonObject createRequest() {
        RegisterToServiceRequest request = new RegisterToServiceRequest();
        request.setLicense(vehicle.getVehicle().plate());
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(eventBusConstants.BODY(), JsonObject.mapFrom(request).toString());
        System.out.println("Ho creato il jsonobject = " + jsonObject.toString());
        return jsonObject;
    }

    private void checkResponse(RegisterToServiceResponse response) {
        System.out.println("CHECKO LA RISPOSTA DEL SERVICE");
        if(!response.getRegisterOk()){
            String newLicensePlate = calculateNewLicensePlate(vehicle);
            registerToService(newLicensePlate);
        }
    }

    private String calculateNewLicensePlate(VehicleControl vehicle) {
        System.out.println("Vecchia targa del veicolo = " + vehicle.getVehicle().plate());
        String newLicense = UUID.randomUUID().toString();
        if(vehicle.getVehicle().plate().equals(newLicense)) {
            String license = calculateNewLicensePlate(vehicle);
            return license;
        }
        vehicle.getVehicle().plate_$eq(newLicense);
        System.out.println("Nuova targa del veicolo = " + vehicle.getVehicle().plate());
        return newLicense;
    }

}
