package com.wedriveu.vehicle.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.shared.entity.ArrivedNotify;
import com.wedriveu.shared.utils.Log;
import com.wedriveu.shared.utils.Position;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.control.VehicleControlImpl;
import com.wedriveu.vehicle.shared.EventBusConstants$;
import com.wedriveu.vehicle.shared.Exchanges$;
import com.wedriveu.vehicle.shared.RoutingKeys$;
import com.weriveu.vehicle.boundary.VehicleVerticleArrivedNotifyImpl;
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

/**
 * @author Michele Donati on 11/08/2017.
 */

@RunWith(VertxUnitRunner.class)
public class VehicleVerticleArrivedNotifyImplTest {

    private static final String TAG = VehicleVerticleArrivedNotifyImplTest.class.getSimpleName();
    private static final String JSON_QUEUE_KEY = "queue";
    private static final String EVENT_BUS_ADDRESS = VehicleVerticleArrivedNotifyImplTest.class.getCanonicalName();

    private EventBusConstants$ eventBusConstants = EventBusConstants$.MODULE$;
    private Exchanges$ exchanges = Exchanges$.MODULE$;
    private RoutingKeys$ routingKeys = RoutingKeys$.MODULE$;
    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private VehicleVerticleArrivedNotifyImpl vehicleVerticle;
    private String requestId;
    private VehicleControl vehicleControl;
    private String license = "VEHICLE4";
    private String state = "available";
    private Position position = new Position(44.1454528, 12.2474513);
    private double battery = 100.0;
    private double speed = 50.0;
    private VehicleStopView stopUi = new VehicleStopViewImpl(1);
    private boolean debugVar = false;
    private ArrivedNotify notifyToSend = new ArrivedNotify();

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        vehicleControl = new VehicleControlImpl(license, state, position, battery, speed, stopUi, debugVar);
        vehicleVerticle = new VehicleVerticleArrivedNotifyImpl(vehicleControl);
        notifyToSend.setLicense(license);
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
                rabbitMQClient.queueBind(requestId, exchanges.VEHICLE(), routingKeys.VEHICLE_ARRIVED(), onQueueBind ->{
                    vertx.deployVerticle(vehicleVerticle, context.asyncAssertSuccess(onDeploy -> {
                        async.complete();}
                    ));
                    async.countDown();
                    context.assertTrue(onQueueBind.succeeded());
                });
                async.countDown();
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
    public void sendArivedNotifyToService(TestContext context) throws Exception {
        checkVehicleNotify(context);
    }

    private void checkVehicleNotify(TestContext context) {
        final Async async = context.async();
        rabbitMQClient.basicConsume(requestId, EVENT_BUS_ADDRESS, onGet -> {
            MessageConsumer<JsonObject> consumer = eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
                JsonObject notifyJson = new JsonObject(msg.body().getString(eventBusConstants.BODY()));
                Log.info(TAG, notifyJson.toString());
                ArrivedNotify notify = notifyJson.mapTo(ArrivedNotify.class);
                context.assertTrue(notify.getLicense().equals(license));
            });
            consumer.exceptionHandler(event -> {
                context.fail(event.getCause());
            });
        });
        vehicleVerticle.sendArrivedNotify(notifyToSend);
        vertx.setTimer(5000, onTime -> {
            async.complete();});
        async.awaitSuccess();
    }

}