package com.wedriveu.services.authentication.boundary;

import com.wedriveu.services.authentication.control.AuthenticationControllerVerticle;
import com.wedriveu.services.authentication.entity.UserStore;
import com.wedriveu.services.authentication.entity.UserStoreImpl;
import com.wedriveu.services.shared.model.User;
import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.shared.rabbitmq.message.LoginRequest;
import com.wedriveu.shared.rabbitmq.message.LoginResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
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
public class AuthenticationBoundaryVerticleTest {

    private static final String JSON_QUEUE_KEY = "queue";
    private static final String EVENT_BUS_ADDRESS = AuthenticationBoundaryVerticleTest.class.getCanonicalName();

    private Vertx vertx;
    private EventBus eventBus;
    private RabbitMQClient rabbitMQClient;
    private UserStore userStore;
    private AuthenticationBoundaryVerticle authenticationBoundary;
    private AuthenticationControllerVerticle authenticationController;
    private String requestId;

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        eventBus = vertx.eventBus();
        userStore = new UserStoreImpl();
        authenticationBoundary = new AuthenticationBoundaryVerticle();
        authenticationController = new AuthenticationControllerVerticle();
        setUpAsyncComponents(context);
    }

    private void setUpAsyncComponents(TestContext context) {
        Async async = context.async(3);
        rabbitMQClient = RabbitMQClientFactory.createClient(vertx);
        rabbitMQClient.start(onStart -> {
            rabbitMQClient.queueDeclareAuto(onQueueDeclare -> {
                requestId = onQueueDeclare.result().getString(JSON_QUEUE_KEY);
                context.assertTrue(onQueueDeclare.succeeded());
                vertx.deployVerticle(authenticationController, context.asyncAssertSuccess(onControllerDeploy -> {
                    vertx.deployVerticle(authenticationBoundary, context.asyncAssertSuccess(onDeploy -> {
                        async.complete();
                    }));
                }));
                async.countDown();
            });
            async.countDown();
        });
        async.awaitSuccess();
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        userStore.clear();
        rabbitMQClient.stop(context.asyncAssertSuccess());
    }

    @Test
    public void checkCredentials(TestContext context) throws Exception {
        final Async async = context.async(2);
        checkServiceResponse(context, async);
        async.awaitSuccess();
    }

    private void checkServiceResponse(TestContext context, Async async) {
        MessageConsumer<JsonObject> consumer = eventBus.consumer(EVENT_BUS_ADDRESS, msg -> {
            JsonObject responseJson = new JsonObject(msg.body().getString(Constants.EventBus.BODY));
            LoginResponse response = responseJson.mapTo(LoginResponse.class);
            context.assertTrue(response.isSuccess());
            async.complete();
        });
        rabbitMQClient.basicConsume(requestId, EVENT_BUS_ADDRESS, onGet -> {
        });
        consumer.exceptionHandler(event -> {
            context.fail(event.getCause());
            async.complete();
        });
        rabbitMQClient.basicPublish(Constants.RabbitMQ.Exchanges.USER,
                Constants.RabbitMQ.RoutingKey.LOGIN,
                createRequestJsonObject(),
                onPublish -> {
                    context.assertTrue(onPublish.succeeded());
                    async.countDown();
                });
    }

    private JsonObject createRequestJsonObject() {
        LoginRequest request = new LoginRequest();
        request.setRequestId(requestId);
        User user = User.USERS[0];
        request.setUsername(user.getUsername());
        request.setPassword(user.getPassword());
        JsonObject jsonObject = new JsonObject();
        jsonObject.put(Constants.EventBus.BODY, JsonObject.mapFrom(request).toString());
        return jsonObject;
    }

}