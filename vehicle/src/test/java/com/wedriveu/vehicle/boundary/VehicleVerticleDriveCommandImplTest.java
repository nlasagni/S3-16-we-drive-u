package com.wedriveu.vehicle.boundary;

import com.wedriveu.shared.rabbitmq.message.DriveCommand;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.control.VehicleControlImpl;
import com.wedriveu.vehicle.shared.VehicleConstants$;
import com.weriveu.vehicle.boundary.VehicleVerticleDriveCommandImpl;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
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
 * @author Michele Donati on 11/08/2017.
 */

@RunWith(VertxUnitRunner.class)
public class VehicleVerticleDriveCommandImplTest {

    private static final double minorBoundPositionLat = 44.145500;
    private static final double maxBoundPositionLat = 44.146612;
    private static final double minorBoundPositionLon = 12.247025;
    private static final double maxBoundPositionLon = 12.247497;
    private static final long TIME_OUT = 4000;

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
    private VehicleVerticleDriveCommandImpl vehicleVerticle;
    private VehicleControl vehicleControl;
    private String license = "VEHICLE6";
    private String state = "available";
    private Position position = new Position(44.1454528, 12.2474513);
    private double battery = 100.0;
    private double speed = 80.0;
    private VehicleStopView stopUi = new VehicleStopViewImpl(1);
    private boolean debugVar = false;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        vehicleControl = new VehicleControlImpl(license, state, position, battery, speed, stopUi, debugVar);
        vehicleVerticle = new VehicleVerticleDriveCommandImpl(vehicleControl);
        setUpAsyncComponents(context);
    }

    private void setUpAsyncComponents(TestContext context) {
        Async async = context.async(2);
        JsonObject config = new JsonObject();
        config.put(Constants.RabbitMQ.ConfigKey.HOST, Constants.RabbitMQ.Broker.HOST);
        config.put(Constants.RabbitMQ.ConfigKey.PASSWORD, Constants.RabbitMQ.Broker.PASSWORD);
        config.put(Constants.RabbitMQ.ConfigKey.PORT, Constants.RabbitMQ.Broker.PORT);
        rabbitMQClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
        rabbitMQClient.start(onStart -> {
            vertx.deployVerticle(vehicleVerticle,
                    new DeploymentOptions().setWorker(true),
                    context.asyncAssertSuccess(onDeploy ->
                        async.complete()
            ));
            async.countDown();
        });
        async.awaitSuccess();
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        rabbitMQClient.stop(context.asyncAssertSuccess());
    }

    @Test
    public void drive(TestContext context) throws Exception {
        final Async async = context.async(2);
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_DRIVE_COMMAND,
                createCommandJsonObject(),
                onPublish -> {
                    context.assertTrue(onPublish.succeeded());
                    checkVehiclePosition(context, async);
                    async.countDown();
                });
        async.awaitSuccess();
    }

    private JsonObject createCommandJsonObject() {
        DriveCommand newCommand = new DriveCommand();
        newCommand.setUserPosition(new Position(randomLatitudeUser, randomLongitudeUser));
        newCommand.setDestinationPosition(new Position(randomLatitudeDestination, randomLongitudeDestination));
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(newCommand).toString());
        return jsonObject;
    }

    private void checkVehiclePosition(TestContext context, Async async) {
        Position desired = new Position(randomLatitudeDestination, randomLongitudeDestination);
        vertx.setTimer(TIME_OUT, onTime -> {
            Position vehiclePosition = vehicleControl.getVehicle().position();
            double vehicleDistance = desired.getDistanceInKm(vehiclePosition);
            context.assertTrue(vehicleDistance <= VehicleConstants$.MODULE$.ARRIVED_MAXIMUM_DISTANCE_IN_KILOMETERS());
            async.complete();
        });
    }

}
