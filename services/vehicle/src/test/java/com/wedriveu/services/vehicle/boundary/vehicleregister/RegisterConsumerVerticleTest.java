package com.wedriveu.services.vehicle.boundary.vehicleregister;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactory;
import com.wedriveu.services.vehicle.boundary.vehicleregister.entity.VehicleFactoryA;
import com.wedriveu.services.vehicle.entity.Vehicle;
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
import sun.jvm.hotspot.asm.Register;

import java.io.IOException;

import static com.wedriveu.services.shared.utilities.Constants.BODY;
import static com.wedriveu.services.shared.utilities.Constants.ROUTING_KEY_REGISTER_VEHICLE_REQUEST;
import static com.wedriveu.services.shared.utilities.Constants.VEHICLE_SERVICE_EXCHANGE;


@RunWith(VertxUnitRunner.class)
public class RegisterConsumerVerticleTest {

    private static final long RESPONSE_TIME = 2000;
    private static final String JSON_QUEUE_KEY = "queue";
    private static final String EVENT_BUS_ADDRESS = RegisterConsumerVerticleTest.class.getCanonicalName();
    private static final String PASSWORD = "password";
    private static final String HOST = "host";
    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private RegisterConsumerVerticle registerConsumerVerticle;
    private ObjectMapper objectMapper;
    private String requestId;
    private static final String TAG = RegisterConsumerVerticle.class.getSimpleName();



    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        registerConsumerVerticle = new RegisterConsumerVerticle();
        objectMapper = new ObjectMapper();
        JsonObject config = new JsonObject();
        config.put(HOST, Constants.RABBITMQ_SERVER_HOST);
        config.put(PASSWORD, Constants.RABBITMQ_SERVER_PASSWORD);
        Async async = context.async(3);
        rabbitMQClient = RabbitMQClient.create(vertx, config);
        rabbitMQClient.start(onStart -> {
            rabbitMQClient.queueDeclareAuto(onQueueDeclare -> {
                requestId = onQueueDeclare.result().getString(JSON_QUEUE_KEY);
                context.assertTrue(onQueueDeclare.succeeded());
                vertx.deployVerticle(registerConsumerVerticle, context.asyncAssertSuccess(onDeploy ->
                        async.complete()
                ));
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
    public void checkCredentials(TestContext context) throws Exception {
        Async async = context.async(3);

            try {
                rabbitMQClient.basicPublish(VEHICLE_SERVICE_EXCHANGE,
                        ROUTING_KEY_REGISTER_VEHICLE_REQUEST,
                        createRequestJsonObject(),
                        onPublish -> {
                            if (!onPublish.succeeded()) {
                                Log.error(TAG, onPublish.cause().getLocalizedMessage(), onPublish.cause());
                            }
                            async.countDown();
                            vertx.setTimer(RESPONSE_TIME, onTimeOut -> {
                                eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {

                                    Log.log("Message received");
                                    JsonObject responseJson = new JsonObject(msg.body().toString());
                                    //LoginResponse response = objectMapper.readValue(responseJson.getString(Constants.EventBusMessage.BODY), LoginResponse.class);
                                    //context.assertTrue();
                                    Log.log(responseJson.encodePrettily());
                                    async.complete();

                                });
                                rabbitMQClient.basicConsume(requestId, EVENT_BUS_ADDRESS, onGet -> {});
                            });
                        });
                async.countDown();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        async.awaitSuccess();
    }

    private JsonObject createRequestJsonObject() throws JsonProcessingException {
        Vehicle vehicle = new VehicleFactoryA().getVehicle();
        JsonObject jsonObject = new JsonObject().mapFrom(vehicle);
        jsonObject.put(BODY, jsonObject);
        return jsonObject;
    }

}
