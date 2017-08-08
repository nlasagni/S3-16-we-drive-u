package com.wedriveu.services.authentication.boundary;

import com.wedriveu.services.shared.rabbitmq.RabbitMQConfig;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.shared.entity.LoginRequest;
import com.wedriveu.shared.entity.LoginResponse;
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
 * @author Nicola Lasagni on 05/08/2017.
 */
@RunWith(VertxUnitRunner.class)
public class AuthenticationServiceVerticleImplTest {

    private static final String TAG = AuthenticationServiceVerticleImplTest.class.getSimpleName();
    private static final String JSON_QUEUE_KEY = "queue";
    private static final String EVENT_BUS_ADDRESS = AuthenticationServiceVerticleImplTest.class.getCanonicalName();
    private static final String USERNAME = "Michele";
    private static final String PASSWORD = "PASSWORD1";

    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private AuthenticationServiceVerticleImpl authenticationService;
    private String requestId;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        authenticationService = new AuthenticationServiceVerticleImpl();
        setUpAsyncComponents(context);
    }

    private void setUpAsyncComponents(TestContext context) {
        Async async = context.async(3);
        rabbitMQClient = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
        rabbitMQClient.start(onStart -> {
            rabbitMQClient.queueDeclareAuto(onQueueDeclare -> {
                requestId = onQueueDeclare.result().getString(JSON_QUEUE_KEY);
                context.assertTrue(onQueueDeclare.succeeded());
                vertx.deployVerticle(authenticationService, context.asyncAssertSuccess(onDeploy ->
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
        final Async async = context.async(2);
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.USER,
                Constants.RabbitMQ.RoutingKey.LOGIN,
                createRequestJsonObject(),
                onPublish -> {
                    context.assertTrue(onPublish.succeeded());
                    checkServiceResponse(context, async);
                    async.countDown();
                });
        async.awaitSuccess();
    }

    private void checkServiceResponse(TestContext context, Async async) {
        rabbitMQClient.basicConsume(requestId, EVENT_BUS_ADDRESS, onGet -> {});
        MessageConsumer<JsonObject> consumer = eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
                JsonObject responseJson = new JsonObject(msg.body().getString(Constants.EventBus.BODY));
                Log.info(TAG, responseJson.toString());
                LoginResponse response = responseJson.mapTo(LoginResponse.class);
                context.assertTrue(response.getSuccess());
                async.complete();
        });
        consumer.exceptionHandler(event -> {
            context.fail(event.getCause());
            async.complete();
        });
    }

    private JsonObject createRequestJsonObject() {
        LoginRequest request = new LoginRequest();
        request.setRequestId(requestId);
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(request).toString());
        return jsonObject;
    }

}