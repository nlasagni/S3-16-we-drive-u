package com.wedriveu.vehicle.boundary;

import com.wedriveu.shared.rabbitmq.message.ArrivedNotify;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.control.VehicleControlImpl;
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

    private static final String QUEUE = VehicleVerticleArrivedNotifyImplTest.class.getCanonicalName();
    private static final String EVENT_BUS_ADDRESS =
            VehicleVerticleArrivedNotifyImplTest.class.getCanonicalName();

    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private VehicleVerticleArrivedNotifyImpl vehicleVerticle;
    private VehicleControl vehicleControl;
    private String license = "VEHICLE4";
    private String state = "available";
    private double battery = 100.0;
    private double speed = 50.0;
    private VehicleStopView stopUi;
    private boolean debugVar = true;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        stopUi = new VehicleStopViewImpl(vertx, 1);
        vehicleControl =
                new VehicleControlImpl(vertx,
                        "",
                        "",
                        license,
                        state,
                        Constants.HEAD_QUARTER,
                        battery,
                        speed,
                        stopUi,
                        debugVar);
        vehicleVerticle = new VehicleVerticleArrivedNotifyImpl(vehicleControl);
        setUpAsyncComponents(context);
    }

    private void setUpAsyncComponents(TestContext context) {
        Async async = context.async();
        JsonObject config = new JsonObject();
        config.put(Constants.RabbitMQ.ConfigKey.HOST, Constants.RabbitMQ.Broker.HOST);
        config.put(Constants.RabbitMQ.ConfigKey.PASSWORD, Constants.RabbitMQ.Broker.PASSWORD);
        config.put(Constants.RabbitMQ.ConfigKey.PORT, Constants.RabbitMQ.Broker.PORT);
        rabbitMQClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
        rabbitMQClient.start(onStart ->
                rabbitMQClient.queueDeclare(QUEUE,
                        false,
                        false,
                        true,
                        onQueueDeclare -> {
                            rabbitMQClient.queueBind(QUEUE,
                                    Constants.RabbitMQ.Exchanges.VEHICLE,
                                    Constants.RabbitMQ.RoutingKey.VEHICLE_ARRIVED,
                                    onQueueBind -> {
                                        vertx.deployVerticle(vehicleVerticle,
                                                context.asyncAssertSuccess(onDeploy -> async.complete()));
                                        context.assertTrue(onQueueBind.succeeded());
                                    });
                            context.assertTrue(onQueueDeclare.succeeded());
                        })
        );
        async.awaitSuccess();
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        rabbitMQClient.stop(context.asyncAssertSuccess());
    }

    @Test
    public void sendArrivedNotifyToService(TestContext context) throws Exception {
        final Async async = context.async();
        MessageConsumer<JsonObject> consumer = eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            JsonObject notifyJson = new JsonObject(msg.body().getString(Constants.EventBus.BODY));
            ArrivedNotify notify = notifyJson.mapTo(ArrivedNotify.class);
            context.assertTrue(notify.getLicense().equals(license));
            async.complete();
        });
        consumer.exceptionHandler(event -> {
            context.fail(event.getCause());
        });
        rabbitMQClient.basicConsume(QUEUE, EVENT_BUS_ADDRESS, onGet -> {
        });
        eventBus.send(String.format(Constants.EventBus.EVENT_BUS_ADDRESS_NOTIFY,
                vehicleControl.getVehicle().plate()),
                new JsonObject());
        async.awaitSuccess();
    }

}
