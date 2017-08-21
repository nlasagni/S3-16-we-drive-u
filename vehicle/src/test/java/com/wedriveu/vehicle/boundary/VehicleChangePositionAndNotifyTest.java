package com.wedriveu.vehicle.boundary;

import com.wedriveu.shared.rabbitmq.message.ArrivedNotify;
import com.wedriveu.shared.rabbitmq.message.DriveCommand;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.shared.util.Position;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.control.VehicleControlImpl;
import com.wedriveu.vehicle.shared.VehicleConstants$;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rabbitmq.RabbitMQClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Michele Donati on 16/08/2017.
 */

@RunWith(VertxUnitRunner.class)
public class VehicleChangePositionAndNotifyTest {

    private static final String TAG = VehicleChangePositionAndNotifyTest.class.getSimpleName();
    private static final String JSON_QUEUE_KEY = "queue";
    private static final String EVENT_BUS_ADDRESS = VehicleChangePositionAndNotifyTest.class.getCanonicalName();

    private static final double minorBoundPositionLat = 44.145500;
    private static final double maxBoundPositionLat = 44.145501;
    private static final double minorBoundPositionLon = 12.247497;
    private static final double maxBoundPositionLon = 12.247498;
    private static final int timeToSleep = 1000;

    private double randomLatitudeUser =
            ThreadLocalRandom.current().nextDouble(minorBoundPositionLat, maxBoundPositionLat);
    private double randomLongitudeUser =
            ThreadLocalRandom.current().nextDouble(minorBoundPositionLon, maxBoundPositionLon);
    private double randomLatitudeDestination =
            ThreadLocalRandom.current().nextDouble(minorBoundPositionLat, maxBoundPositionLat);
    private double randomLongitudeDestination =
            ThreadLocalRandom.current().nextDouble(minorBoundPositionLon, maxBoundPositionLon);

    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private VehicleVerticleDriveCommandImpl vehicleVerticleDriveCommand;
    private VehicleVerticleArrivedNotifyImpl vehicleVerticleArrivedNotify;
    private VehicleControl vehicleControl;
    private String requestId;
    private String license = "VEHICLE7";
    private String state = "available";
    private Position position = new Position(44.1454528, 12.2474513);
    private double battery = 100.0;
    private double speed = 80.0;
    private VehicleStopView stopUi;
    private boolean debugVar = true;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        stopUi = new VehicleStopViewImpl(vertx, 1);
        vehicleControl =
                new VehicleControlImpl(vertx,"","",license, state, position, battery, speed, stopUi, debugVar);
        vehicleControl.setUserOnBoard(true);
        vehicleVerticleDriveCommand = new VehicleVerticleDriveCommandImpl(vehicleControl, false);
        vehicleVerticleArrivedNotify = new VehicleVerticleArrivedNotifyImpl(vehicleControl);
        setUpAsyncComponents(context);
    }

    private void setUpAsyncComponents(TestContext context) {
        Async async = context.async(4);
        JsonObject config = new JsonObject();
        config.put(Constants.RabbitMQ.ConfigKey.HOST, Constants.RabbitMQ.Broker.HOST);
        config.put(Constants.RabbitMQ.ConfigKey.PASSWORD, Constants.RabbitMQ.Broker.PASSWORD);
        config.put(Constants.RabbitMQ.ConfigKey.PORT, Constants.RabbitMQ.Broker.PORT);
        rabbitMQClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
        rabbitMQClient.start(onStart -> {
            rabbitMQClient.queueDeclareAuto(onQueueDeclare -> {
                requestId = onQueueDeclare.result().getString(JSON_QUEUE_KEY);
                rabbitMQClient.queueBind(requestId,
                        Constants.RabbitMQ.Exchanges.VEHICLE,
                        Constants.RabbitMQ.RoutingKey.VEHICLE_ARRIVED,
                        onQueueBind -> {
                            vertx.deployVerticle(vehicleVerticleDriveCommand,
                                    new DeploymentOptions().setWorker(true),
                                    context.asyncAssertSuccess(onDeploy ->{}
                            ));
                            vertx.deployVerticle(vehicleVerticleArrivedNotify,
                                    new DeploymentOptions().setWorker(true),
                                    context.asyncAssertSuccess(onDeploy ->
                                    async.complete()
                            ));
                            async.countDown();
                            context.assertTrue(onQueueBind.succeeded());
                        });
                context.assertTrue(onQueueDeclare.succeeded());
                async.countDown();
            });
            async.countDown();
        });
        async.awaitSuccess();
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        rabbitMQClient.stop(context.asyncAssertSuccess());
    }

    @Test
    public void driveAndAwaitNotify(TestContext context) throws Exception {
        final Async async = context.async(2);
        vehicleControl.setUserOnBoard(true);
        vehicleControl.getVehicle().setPosition(position);
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_DRIVE_COMMAND, vehicleControl.getVehicle().getPlate()),
                createCommandJsonObject(),
                onPublish -> {
                    context.assertTrue(onPublish.succeeded());
                    checkVehicleNotify(context, async);
                    async.countDown();
                });
    }

    private JsonObject createCommandJsonObject() {
        DriveCommand newCommand = new DriveCommand();
        newCommand.setUserPosition(new Position(randomLatitudeUser, randomLongitudeUser));
        newCommand.setDestinationPosition(new Position(randomLatitudeDestination, randomLongitudeDestination));
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(newCommand).toString());
        return jsonObject;
    }

    private void checkVehicleNotify(TestContext context, Async async) {
        Position desired = new Position(randomLatitudeDestination, randomLongitudeDestination);
        try {
            Thread.sleep(timeToSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Position vehiclePosition = vehicleControl.getVehicle().position();
        double vehicleDistance = desired.getDistanceInKm(vehiclePosition);
        if (vehicleDistance <= VehicleConstants$.MODULE$.ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS()) {}
        rabbitMQClient.basicConsume(requestId, EVENT_BUS_ADDRESS, onGet -> {});
        MessageConsumer<JsonObject> consumer = eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            JsonObject responseJson = new JsonObject(msg.body().getString(Constants.EventBus.BODY));
            Log.info(TAG, responseJson.toString());
            ArrivedNotify notify =  responseJson.mapTo(ArrivedNotify.class);
            context.assertTrue(notify.getLicense().equals(license));
            async.complete();
        });
        consumer.exceptionHandler(event -> {
            context.fail(event.getCause());
            async.complete();
        });
    }

}
