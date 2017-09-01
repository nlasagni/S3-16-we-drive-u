package com.wedriveu.vehicle.boundary;

import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.shared.VehicleConstants$;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * @author Michele Donati on 11/08/2017.
 */

public class VehicleVerticleUpdateImpl extends AbstractVerticle implements VehicleVerticleUpdate {

    private VehicleControl vehicle;
    private static final String TAG = VehicleVerticleUpdateImpl.class.getSimpleName();
    private static final String SEND_ERROR = "Error occurred while sending request.";
    private static final String FAILURE_MESSAGE = "The vehicle is broken/stolen";
    private static final String NOT_FAILURE_MESSAGE = "The vehicle is not broken/stolen";

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;
    private VehicleConstants$ vehicleConstants = VehicleConstants$.MODULE$;

    public VehicleVerticleUpdateImpl(VehicleControl vehicle) {
        this.vehicle = vehicle;
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        eventBus = vertx.eventBus();
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
            registerConsumer();
            future.complete();
        }, endFuture);
    }

    private void startClient(Future<Void> future) {
        rabbitMQClient.start(future.completer());
    }


    private void registerConsumer() {
        eventBus.consumer(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_UPDATE, vehicle.getVehicle().plate()),
                message -> {
                    sendUpdate();
                });
    }

    @Override
    public void sendUpdate() {
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE,
                createUpdate(),
                onPublish -> {
                    onPublish.succeeded();
                    if (onPublish.failed()) {
                        Log.error(TAG, SEND_ERROR);
                    }
                });
    }

    private JsonObject createUpdate() {
        VehicleUpdate vehicleUpdate = new VehicleUpdate();
        vehicleUpdate.setPosition(vehicle.getVehicle().position());
        vehicleUpdate.setLicense(vehicle.getVehicle().plate());
        String state = vehicle.getVehicle().getState();
        vehicleUpdate.setStatus(state);
        vehicleUpdate.setUserOnBoard(vehicle.getUserOnBoard());
        vehicleUpdate.setUsername(vehicle.getUsername());
        String message;
        if (state.equals(Constants.Vehicle.STATUS_BROKEN_STOLEN)) {
            message = FAILURE_MESSAGE;
        } else {
            message = NOT_FAILURE_MESSAGE;
        }
        vehicleUpdate.setFailureMessage(message);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(vehicleUpdate).toString());
        return jsonObject;
    }

}
