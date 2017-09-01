package com.wedriveu.vehicle.boundary;

import com.wedriveu.shared.rabbitmq.message.ArrivedNotify;
import com.wedriveu.shared.rabbitmq.message.DriveCommand;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;
import com.wedriveu.vehicle.boundary.mock.UserEnterVehicleMockVerticle;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.control.VehicleControlImpl;
import com.wedriveu.vehicle.shared.VehicleConstants$;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
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

import static com.wedriveu.shared.util.Constants.HEAD_QUARTER;

/**
 * @author Michele Donati on 16/08/2017.
 * @author Nicola Lasagni
 */

@RunWith(VertxUnitRunner.class)
public class VehicleChangePositionAndNotifyTest {

    private static final String TAG = VehicleChangePositionAndNotifyTest.class.getSimpleName();
    private static final String JSON_QUEUE_KEY = "queue";
    private static final String EVENT_BUS_ADDRESS = VehicleChangePositionAndNotifyTest.class.getCanonicalName();

    private static final double USER_LATITUDE = 44.138519;
    private static final double USER_LONGITUDE = 12.241193;
    private static final double DESTINATION_LATITUDE = 44.222603;
    private static final double DESTINATION_LONGITUDE = 12.038920;
    private static final double BATTERY = 100.0;
    private static final double SPEED = 80.0;
    private static final Position USER_POSITION = new Position(USER_LATITUDE, USER_LONGITUDE);
    private static final Position DESTINATION_POSITION = new Position(DESTINATION_LATITUDE, DESTINATION_LONGITUDE);
    private static final String USERNAME = "username";
    private static final String LICENSE = "VEHICLE7";

    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private VehicleVerticleDriveCommandImpl vehicleVerticleDriveCommand;
    private VehicleVerticleArrivedNotifyImpl vehicleVerticleArrivedNotify;
    private VehicleVerticleForUserImpl vehicleVerticleForUser;
    private UserEnterVehicleMockVerticle userEnterVehicleMockVerticle;
    private VehicleControl vehicleControl;
    private String requestId;
    private VehicleStopView stopUi;
    private boolean debugVar = true;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        stopUi = new VehicleStopViewImpl(vertx, 1);
        vehicleControl = createControl();
        vehicleControl.setUserOnBoard(true);
        vehicleVerticleDriveCommand = new VehicleVerticleDriveCommandImpl(vehicleControl, false);
        vehicleVerticleArrivedNotify = new VehicleVerticleArrivedNotifyImpl(vehicleControl);
        vehicleVerticleForUser = new VehicleVerticleForUserImpl(vehicleControl);
        userEnterVehicleMockVerticle = new UserEnterVehicleMockVerticle(USERNAME, LICENSE);
        setUpAsyncComponents(context);
    }

    private VehicleControlImpl createControl() {
        return new VehicleControlImpl(vertx,
                "",
                "",
                LICENSE,
                Constants.Vehicle.STATUS_AVAILABLE,
                HEAD_QUARTER,
                BATTERY,
                SPEED,
                stopUi,
                debugVar);
    }

    private void setUpAsyncComponents(TestContext context) {
        Async async = context.async(2);
        Future<Void> rabbitMqFuture = Future.future();
        Future<String> vertxFuture = Future.future();
        vertxFuture.setHandler(handler -> {
            async.complete();
        });
        rabbitMqFuture.setHandler(handler -> {
            async.countDown();
            deployVehicleVerticles(context, vertxFuture);
        });
        setUpRabbitMq(context, rabbitMqFuture);
        async.awaitSuccess();
    }

    private void setUpRabbitMq(TestContext context, Future<Void> future) {
        JsonObject config = new JsonObject();
        config.put(Constants.RabbitMQ.ConfigKey.HOST, Constants.RabbitMQ.Broker.HOST);
        config.put(Constants.RabbitMQ.ConfigKey.PASSWORD, Constants.RabbitMQ.Broker.PASSWORD);
        config.put(Constants.RabbitMQ.ConfigKey.PORT, Constants.RabbitMQ.Broker.PORT);
        rabbitMQClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
        rabbitMQClient.start(onStart ->
            rabbitMQClient.queueDeclareAuto(onQueueDeclare -> {
                requestId = onQueueDeclare.result().getString(JSON_QUEUE_KEY);
                rabbitMQClient.queueBind(requestId,
                        Constants.RabbitMQ.Exchanges.VEHICLE,
                        Constants.RabbitMQ.RoutingKey.VEHICLE_ARRIVED,
                        onQueueBind -> {});
                context.assertTrue(onQueueDeclare.succeeded());
                future.complete();
            })
        );
    }

    private void deployVehicleVerticles(TestContext context, Future<String> future) {
        vertx.deployVerticle(vehicleVerticleDriveCommand,
                new DeploymentOptions().setWorker(true),
                context.asyncAssertSuccess(onDriveDeploy ->
                        vertx.deployVerticle(vehicleVerticleArrivedNotify,
                                new DeploymentOptions().setWorker(true),
                                context.asyncAssertSuccess(onArrivedDeploy ->
                                        vertx.deployVerticle(vehicleVerticleForUser,
                                                context.asyncAssertSuccess(onUserDeploy -> {
                                                    future.complete();
                                                })
                                        )
                                )
                        )
                )
        );
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        rabbitMQClient.stop(context.asyncAssertSuccess());
    }

    @Test
    public void driveAndAwaitNotify(TestContext context) throws Exception {
        final Async async = context.async();
        vehicleControl.setUsername(USERNAME);
        vehicleControl.getVehicle().setPosition(HEAD_QUARTER);
        checkVehicleNotify(context, async);
    }

    private JsonObject createCommandJsonObject() {
        DriveCommand newCommand = new DriveCommand();
        newCommand.setUserPosition(USER_POSITION);
        newCommand.setDestinationPosition(DESTINATION_POSITION);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(newCommand).toString());
        return jsonObject;
    }

    private void checkVehicleNotify(TestContext context, Async async) {
        Future<String> mockFuture = Future.future();
        Future<Void> waitVehicleFuture = Future.future();
        waitVehicleFuture.setHandler(handler -> sendDriveCommand(context));
        mockFuture.setHandler(handler -> waitVehicleToArrive(context, async, waitVehicleFuture));
        mockUser(mockFuture);
    }

    private void sendDriveCommand(TestContext context) {
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_DRIVE_COMMAND,
                        vehicleControl.getVehicle().getPlate()),
                createCommandJsonObject(),
                onPublish -> context.assertTrue(onPublish.succeeded()));
    }

    private void waitVehicleToArrive(TestContext context, Async async, Future<Void> future) {
        MessageConsumer<JsonObject> consumer = eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            Position vehiclePosition = vehicleControl.getVehicle().position();
            double vehicleDistance = DESTINATION_POSITION.getDistanceInKm(vehiclePosition);
            JsonObject responseJson = new JsonObject(msg.body().getString(Constants.EventBus.BODY));
            ArrivedNotify notify = responseJson.mapTo(ArrivedNotify.class);
            context.assertTrue(notify.getLicense().equals(LICENSE) &&
                    vehicleDistance <= VehicleConstants$.MODULE$.ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS());
            async.complete();
        });
        consumer.exceptionHandler(event -> {
            context.fail(event.getCause());
            async.complete();
        });
        rabbitMQClient.basicConsume(requestId, EVENT_BUS_ADDRESS, future.completer());
    }

    private void mockUser(Future<String> future) {
        vertx.deployVerticle(userEnterVehicleMockVerticle, future.completer());
    }

}
