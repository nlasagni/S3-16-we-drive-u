package com.wedriveu.vehicle.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.shared.rabbitmq.message.RegisterToServiceResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.control.VehicleControlImpl;
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

/**
 * @author Michele Donati on 10/08/2017.
 */

@RunWith(VertxUnitRunner.class)
public class VehicleVerticleRegisterImplTest {

    private static final String TAG = VehicleVerticleRegisterImplTest.class.getSimpleName();
    private static final String JSON_QUEUE_KEY = "queue";
    private static final String EVENT_BUS_ADDRESS = VehicleVerticleRegisterImplTest.class.getSimpleName();

    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private ObjectMapper objectMapper;
    private VehicleVerticleRegisterImpl vehicleVerticle;
    private String requestId;
    private VehicleControl vehicleControl;
    private String license = "VEHICLE2";
    private String[] licenseList = {"VEHICLE1", "VEHICLE3", "VEHICLE4", "VEHICLE5"};
    private String state = "";
    private double battery = 100.0;
    private double speed = 50.0;
    private VehicleStopView stopUi;
    private boolean debugVar = true;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        objectMapper = new ObjectMapper();
        stopUi = new VehicleStopViewImpl(vertx, 1);
        vehicleControl =
                new VehicleControlImpl(vertx,
                        "http://www.google.com",
                        "",
                        license, state, Constants.HEAD_QUARTER, battery, speed, stopUi, debugVar);
        vehicleVerticle = new VehicleVerticleRegisterImpl(vehicleControl);
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
                        Constants.RabbitMQ.RoutingKey.REGISTER_REQUEST,
                        onQueueBind -> {
                            vertx.deployVerticle(vehicleVerticle,
                                    new DeploymentOptions().setWorker(true),
                                    context.asyncAssertSuccess(onDeploy -> {
                                                async.complete();
                                            }
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
    public void registerToService(TestContext context) throws Exception {
        checkVehicleRequest(context);
    }

    private void checkVehicleRequest(TestContext context) {
        final Async async = context.async();
        rabbitMQClient.basicConsume(requestId, EVENT_BUS_ADDRESS, onGet -> {
            MessageConsumer<JsonObject> consumer = eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
                JsonObject requestJson = new JsonObject(msg.body().getString(Constants.EventBus.BODY));
                Log.info(TAG, requestJson.toString());
                Vehicle request = requestJson.mapTo(Vehicle.class);
                RegisterToServiceResponse response = new RegisterToServiceResponse();
                response.setRegisterOk(checkLicenseList(request.getLicensePlate()));
                sendResponse(response, context);
            });
            consumer.exceptionHandler(event -> {
                context.fail(event.getCause());
            });
        });
        vertx.setTimer(8000, onTime -> {
            async.complete();
        });
        async.awaitSuccess();
    }

    private boolean checkLicenseList(String license) {
        for (int i = 0; i < licenseList.length; i++) {
            if (license.equals(licenseList[i])) {
                return false;
            }
        }
        return true;
    }

    private void sendResponse(RegisterToServiceResponse response, TestContext context) {
        try {
            String responseString = objectMapper.writeValueAsString(response);
            JsonObject responseJson = new JsonObject();
            responseJson.put(Constants.EventBus.BODY, responseString);
            rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                    String.format(Constants.RabbitMQ.RoutingKey.REGISTER_RESPONSE, license),
                    responseJson,
                    onPublish -> {
                        context.assertTrue(onPublish.succeeded());
                    });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
