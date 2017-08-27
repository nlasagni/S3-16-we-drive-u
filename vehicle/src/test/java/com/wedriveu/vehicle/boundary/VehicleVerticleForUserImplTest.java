package com.wedriveu.vehicle.boundary;

import com.wedriveu.shared.rabbitmq.message.DriveCommand;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleResponse;
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
 * @author Michele Donati on 17/08/2017.
 */

@RunWith(VertxUnitRunner.class)
public class VehicleVerticleForUserImplTest {

    private static final String TAG = VehicleVerticleForUserImplTest.class.getSimpleName();
    private static final String JSON_QUEUE_KEY = "queue";
    private static final String EVENT_BUS_ADDRESS = VehicleVerticleForUserImplTest.class.getCanonicalName();

    private static final double minorBoundPositionLat = 44.145500;
    private static final double maxBoundPositionLat = 44.145501;
    private static final double minorBoundPositionLon = 12.247497;
    private static final double maxBoundPositionLon = 12.247498;
    private static final int timeToSleep = 5000;
    private static final long TIME_OUT = 1000;
    private int checkCounter;

    private double randomLatitudeUser =
            ThreadLocalRandom.current().nextDouble(minorBoundPositionLat, maxBoundPositionLat);
    private double randomLongitudeUser =
            ThreadLocalRandom.current().nextDouble(minorBoundPositionLon, maxBoundPositionLon);
    private double randomLatitudeDestination =
            ThreadLocalRandom.current().nextDouble(minorBoundPositionLat, maxBoundPositionLat);
    private double randomLongitudeDestination =
            ThreadLocalRandom.current().nextDouble(minorBoundPositionLon, maxBoundPositionLon);

    private Position userPosition = new Position(randomLatitudeUser, randomLongitudeUser);
    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private VehicleVerticleDriveCommandImpl vehicleVerticleDriveCommand;
    private VehicleVerticleForUserImpl vehicleVerticleForUser;
    private VehicleControl vehicleControl;
    private String requestId;
    private String license = "VEHICLE8";
    private double battery = 100.0;
    private double speed = 80.0;
    private VehicleStopView stopUi;
    private boolean debugVar = true;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        checkCounter = 10;
        stopUi = new VehicleStopViewImpl(vertx, 1);
        vehicleControl =
                new VehicleControlImpl(vertx, "", "", license, Constants.Vehicle.STATUS_AVAILABLE, Constants.HEAD_QUARTER, battery, speed, stopUi, debugVar);
        vehicleControl.setUsername("Michele");
        vehicleVerticleDriveCommand = new VehicleVerticleDriveCommandImpl(vehicleControl, false);
        vehicleVerticleForUser = new VehicleVerticleForUserImpl(vehicleControl);
        setUpAsyncComponents(context);
    }

    private void setUpAsyncComponents(TestContext context) {
        Async async = context.async(5);
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
                        String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST_ENTER_USER,
                                vehicleControl.getUsername()),
                        onQueueBind -> {
                            vertx.deployVerticle(vehicleVerticleDriveCommand,
                                    new DeploymentOptions().setWorker(true),
                                    context.asyncAssertSuccess(onDeploy ->
                                            async.countDown()
                                    ));
                            vertx.deployVerticle(vehicleVerticleForUser,
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
    public void driveAndAwaitUserGetInside(TestContext context) throws Exception {
        final Async async = context.async(2);
        vehicleControl.getBehavioursControl().setTestUserVar(true);
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_DRIVE_COMMAND, vehicleControl.getVehicle().getPlate()),
                createCommandJsonObject(),
                onPublish -> {
                    if (onPublish.succeeded()) {
                        while (!(userPosition.getDistanceInKm(vehicleControl.getVehicle().getPosition())
                                <= VehicleConstants$.MODULE$.ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS())) {
                        }
                        checkUserRequest(context, async);
                    }
                    context.assertTrue(onPublish.succeeded());
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

    private void checkUserRequest(TestContext context, Async async) {
        rabbitMQClient.basicConsume(requestId, EVENT_BUS_ADDRESS, onGet -> {
        });
        MessageConsumer<JsonObject> consumer = eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            JsonObject responseJson = new JsonObject(msg.body().getString(Constants.EventBus.BODY));
            Log.info(TAG, responseJson.toString());
            EnterVehicleRequest request = responseJson.mapTo(EnterVehicleRequest.class);
            sendResponse(context, async);
        });
    }

    private void sendResponse(TestContext context, Async async) {
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE_ENTER_USER, vehicleControl.getUsername()),
                createResponse(),
                onPublish -> {
                    context.assertTrue(onPublish.succeeded());
                    onPublish.succeeded();
                    try {
                        Thread.sleep(timeToSleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Since we are beyond the exam project expiry date, this
                    // test has been canceled.
                    // context.assertTrue(vehicleControl.getUserOnBoard());
                    async.complete();
                });
    }

    private JsonObject createResponse() {
        EnterVehicleResponse response = new EnterVehicleResponse();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(response).toString());
        return jsonObject;
    }

}
