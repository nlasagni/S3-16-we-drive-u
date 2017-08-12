package com.wedriveu.vehicle.boundary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.rabbitmq.RabbitMQConfig;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.shared.entity.RegisterToServiceRequest;
import com.wedriveu.shared.entity.RegisterToServiceResponse;
import com.wedriveu.shared.utils.Log;
import com.wedriveu.shared.utils.Position;
import com.wedriveu.vehicle.control.VehicleControl;
import com.wedriveu.vehicle.control.VehicleControlImpl;
import com.wedriveu.vehicle.shared.EventBusConstants$;
import com.wedriveu.vehicle.shared.Exchanges$;
import com.wedriveu.vehicle.shared.RoutingKeys$;
import com.weriveu.vehicle.boundary.VehicleVerticleRegisterImpl;
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

    private EventBusConstants$ eventBusConstants = EventBusConstants$.MODULE$;
    private Exchanges$ exchanges = Exchanges$.MODULE$;
    private RoutingKeys$ routingKeys = RoutingKeys$.MODULE$;
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
    private Position position = new Position(44.1454528, 12.2474513);
    private double battery = 100.0;
    private double speed = 50.0;
    private VehicleStopView stopUi = new VehicleStopViewImpl(1);
    private boolean debugVar = false;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        objectMapper = new ObjectMapper();
        vehicleControl = new VehicleControlImpl(license, state, position, battery, speed, stopUi, debugVar);
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
                    rabbitMQClient.queueBind(requestId, exchanges.VEHICLE(), routingKeys.REGISTER_REQUEST(), onQueueBind ->{
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
    public void registerToService(TestContext context) throws Exception {
        checkVehicleRequest(context);
    }

    private void checkVehicleRequest(TestContext context) {
        final Async async = context.async();
        rabbitMQClient.basicConsume(requestId, EVENT_BUS_ADDRESS, onGet -> {
            MessageConsumer<JsonObject> consumer = eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
                JsonObject requestJson = new JsonObject(msg.body().getString(eventBusConstants.BODY()));
                Log.info(TAG, requestJson.toString());
                RegisterToServiceRequest request = requestJson.mapTo(RegisterToServiceRequest.class);
                RegisterToServiceResponse response = new RegisterToServiceResponse();
                response.setRegisterOk(checkLicenseList(request.getLicense()));
                sendResponse(response, context);
            });
            consumer.exceptionHandler(event -> {
                context.fail(event.getCause());
            });
        });
        vertx.setTimer(8000, onTime -> {
            async.complete();});
        async.awaitSuccess();
    }

    private boolean checkLicenseList(String license) {
        boolean ok = true;
        for(int i = 0; i < licenseList.length; i++) {
            if(license.equals(licenseList[i])){
                ok = false;
                return ok;
            }
        }
        return ok;
    }

    private void sendResponse(RegisterToServiceResponse response, TestContext context) {
        try {
            String responseString = objectMapper.writeValueAsString(response);
            JsonObject responseJson = new JsonObject();
            responseJson.put(eventBusConstants.BODY(), responseString);
            rabbitMQClient.basicPublish(exchanges.VEHICLE(),
                    String.format(routingKeys.REGISTER_RESPONSE(), license),
                    responseJson,
                    onPublish -> {
                        context.assertTrue(onPublish.succeeded());
                    });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}