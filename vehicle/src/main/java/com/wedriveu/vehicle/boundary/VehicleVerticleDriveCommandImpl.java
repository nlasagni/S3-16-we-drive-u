package com.wedriveu.vehicle.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.shared.rabbitmq.message.DriveCommand;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.vehicle.control.VehicleControl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import java.io.IOException;

/**
 * @author Michele Donati on 11/08/2017.
 */

public class VehicleVerticleDriveCommandImpl extends AbstractVerticle implements VehicleVerticleDriveCommand {

    private VehicleControl vehicle;
    private boolean testVar;
    private static final String TAG = VehicleVerticleDriveCommandImpl.class.getSimpleName();
    private static final String EVENT_BUS_ADDRESS = "vehicle.drivecommandbus";
    private static final String READ_ERROR = "Error occurred while reading request.";
    private String queue;

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;
    private ObjectMapper objectMapper;

    public VehicleVerticleDriveCommandImpl(VehicleControl vehicle, boolean testVar) {
        this.vehicle = vehicle;
        this.testVar = testVar;
        queue = "vehicle.drivecommand." + this.vehicle.getVehicle().plate();
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
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_DRIVE_COMMAND, vehicle.getVehicle().getPlate()),
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(queue, EVENT_BUS_ADDRESS, future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            try {
                JsonObject message = new JsonObject(msg.body().toString());
                DriveCommand driveCommand =
                        objectMapper.readValue(message.getString(Constants.EventBus.BODY), DriveCommand.class);
                drive(driveCommand);
            } catch (IOException e) {
                Log.error(TAG, READ_ERROR, e);
            }
        });
    }

    @Override
    public void drive(DriveCommand driveCommand) {
        vehicle.changePositionUponBooking(driveCommand.getUserPosition(),
                driveCommand.getDestinationPosition(),
                this.testVar);
    }

}
