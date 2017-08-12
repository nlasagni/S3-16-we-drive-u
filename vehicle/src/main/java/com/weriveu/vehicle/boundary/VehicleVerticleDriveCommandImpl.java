package com.weriveu.vehicle.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.shared.entity.DriveCommand;
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

/**
 * @author Michele Donati on 11/08/2017.
 */

public class VehicleVerticleDriveCommandImpl extends AbstractVerticle implements VehicleVerticleDriveCommand {

    private VehicleControl vehicle;
    private static final String TAG = VehicleVerticleDriveCommandImpl.class.getSimpleName();
    private static final String EVENT_BUS_ADDRESS = "vehicle.book";
    private static final String READ_ERROR = "Error occurred while reading request.";
    private static String QUEUE_NAME = "vehicle.drivecommand.";

    private RabbitMQClient rabbitMQClient;
    private EventBus eventBus;
    private ObjectMapper objectMapper;
    private Exchanges$ exchanges = Exchanges$.MODULE$;
    private RoutingKeys$ routingKeys = RoutingKeys$.MODULE$;
    private EventBusConstants$ eventBusConstants = EventBusConstants$.MODULE$;

    public VehicleVerticleDriveCommandImpl(VehicleControl vehicle) {
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
                exchanges.VEHICLE(),
                routingKeys.VEHICLE_DRIVE_COMMAND(),
                future.completer());
    }

    private void basicConsume(Future<Void> future) {
        rabbitMQClient.basicConsume(QUEUE_NAME, EVENT_BUS_ADDRESS, future.completer());
    }

    private void registerConsumer() {
        eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            try {
                JsonObject message = new JsonObject(msg.body().toString());
                DriveCommand driveCommand =
                        objectMapper.readValue(message.getString(eventBusConstants.BODY()), DriveCommand.class);
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
                true);
    }

}
