package com.wedriveu.vehicle.boundary;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.shared.entity.VehicleBookRequest;
import com.wedriveu.shared.entity.VehicleBookResponse;
import com.wedriveu.shared.utils.Log;
import com.wedriveu.shared.utils.Position;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.control.VehicleControlImpl;
import com.wedriveu.vehicle.shared.EventBusConstants$;
import com.wedriveu.vehicle.shared.Exchanges$;
import com.wedriveu.vehicle.shared.RoutingKeys$;
import com.weriveu.vehicle.boundary.VehicleVerticleBookImpl;
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
public class VehicleVerticleBookImplTest {

    private static final String TAG = VehicleVerticleBookImplTest.class.getSimpleName();
    private static final String JSON_QUEUE_KEY = "queue";
    private static final String EVENT_BUS_ADDRESS = VehicleVerticleBookImplTest.class.getCanonicalName();
    private static final String USERNAME = "Michele";
    private static final String license = "VEHICLE3";

    private EventBusConstants$ eventBusConstants = EventBusConstants$.MODULE$;
    private Exchanges$ exchanges = Exchanges$.MODULE$;
    private RoutingKeys$ routingKeys = RoutingKeys$.MODULE$;
    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private VehicleVerticleBookImpl vehicleVerticle;
    private String requestId;
    private VehicleControl vehicleControl;
    private String state = "available";
    private Position position = new Position(44.1454528, 12.2474513);
    private double battery = 100.0;
    private double speed = 50.0;
    private VehicleStopView stopUi = new VehicleStopViewImpl(1);
    private boolean debugVar = false;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        vehicleControl = new VehicleControlImpl(license, state, position, battery, speed, stopUi, debugVar);
        vehicleVerticle = new VehicleVerticleBookImpl(vehicleControl);
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
                        exchanges.VEHICLE(),
                        String.format(routingKeys.BOOK_RESPONSE(), license),
                        onQueueBind -> {
                            vertx.deployVerticle(vehicleVerticle, context.asyncAssertSuccess(onDeploy ->
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
    public void canBeBooked(TestContext context) throws Exception {
        final Async async = context.async(2);
        rabbitMQClient.basicPublish(exchanges.VEHICLE(),
                String.format(routingKeys.BOOK_REQUEST(), license),
                createRequestJsonObject(),
                onPublish -> {
                    context.assertTrue(onPublish.succeeded());
                    checkVehicleBookedResponse(context, async);
                    async.countDown();
                });
        async.awaitSuccess();
    }

    private void checkVehicleBookedResponse(TestContext context, Async async) {
        rabbitMQClient.basicConsume(requestId, EVENT_BUS_ADDRESS, onGet -> {});
        MessageConsumer<JsonObject> consumer = eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            JsonObject responseJson = new JsonObject(msg.body().getString(eventBusConstants.BODY()));
            Log.info(TAG, responseJson.toString());
            VehicleBookResponse response = responseJson.mapTo(VehicleBookResponse.class);
            context.assertTrue(response.getBooked());
            async.complete();
        });
        consumer.exceptionHandler(event -> {
            context.fail(event.getCause());
            async.complete();
        });
    }

    private JsonObject createRequestJsonObject() {
        VehicleBookRequest request = new VehicleBookRequest();
        request.setUsername(USERNAME);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(eventBusConstants.BODY(), JsonObject.mapFrom(request).toString());
        return jsonObject;
    }

}
