package com.wedriveu.services.vehicle.boundary.util;

import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.shared.util.Constants.EventBus.BODY;

/**
 * Basic RabbitMQ communication client (publisher and consumer) for testing VehicleService
 * inbound and outbound communication.
 *
 * @author Marco on 09/08/2017.
 * @author Nicola Lasagni
 */
public abstract class BaseInteractionClient {

    private RabbitMQClient rabbitMQClient;
    private String queue;
    private String consumerExchangeName;
    private String consumerRoutingKey;
    private String eventBusAddress;
    private Vertx vertx;

    /**
     * Instantiates a new BaseInteractionClient.
     *
     * @param queue                the queue used to receive messages
     * @param consumerExchangeName the consumer exchange name used to receive messages
     * @param consumerRoutingKey   the consumer routing key used to receive messages
     * @param eventBusAddress      the event bus address to which redirect RabbitMQ messages
     */
    public BaseInteractionClient(String queue,
                                 String consumerExchangeName,
                                 String consumerRoutingKey,
                                 String eventBusAddress) {
        this.queue = queue;
        this.consumerExchangeName = consumerExchangeName;
        this.consumerRoutingKey = consumerRoutingKey;
        this.eventBusAddress = eventBusAddress;
    }

    /**
     * Sets up a {@link RabbitMQClient}.
     *
     * @param vertx   the {@link Vertx} instance needed for the setup
     * @param handler the handler called when this operation is finished
     */
    protected void setup(Vertx vertx, Handler<AsyncResult<Void>> handler) {
        this.vertx = vertx;
        rabbitMQClient = RabbitMQClientFactory.createClient(vertx);
        rabbitMQClient.start(handler);
    }

    private void declareQueue(Handler<AsyncResult<JsonObject>> handler) {
        rabbitMQClient.queueDeclare(queue, true, false, false, handler);
    }

    /**
     * Declares a queue and binds it to a specific exchange and routing key.
     *
     * @param keyName the routing key to which the queue will be bound
     * @param context the {@link TestContext} used to verify the success of this operation
     * @param handler the handler called when this operation is finished
     */
    protected void declareQueueAndBind(String keyName,
                                       TestContext context,
                                       Handler<AsyncResult<Void>> handler) {
        declareQueue(onDeclareCompleted -> {
            context.assertTrue(onDeclareCompleted.succeeded());
            bindQueueToExchange(keyName, handler);
        });
    }

    private void bindQueueToExchange(String keyName, Handler<AsyncResult<Void>> handler) {
        if (keyName != null && !keyName.isEmpty()) {
            consumerRoutingKey = String.format(consumerRoutingKey, keyName);
        }
        rabbitMQClient.queueBind(queue, consumerExchangeName, consumerRoutingKey,
                onBind -> {
                    if (onBind.succeeded()) {
                        handler.handle(Future.succeededFuture());
                    } else {
                        handler.handle(io.vertx.core.Future.failedFuture(onBind.cause().getMessage()));
                    }
                });
    }

    /**
     * Publish message and wait response by using a {@link TestContext}.
     *
     * @param context           the context used to verify the success of this operation
     * @param publishExchange   the publish exchange
     * @param publishRoutingKey the publish routing key
     * @param data              the data to be sent
     */
    protected void publishMessageAndWaitResponse(TestContext context,
                                                 String publishExchange,
                                                 String publishRoutingKey,
                                                 JsonObject data) {
        Async async = context.async();
        handleServiceResponse(context, async, eventBusAddress);
        rabbitMQClient.basicConsume(queue, eventBusAddress, context.asyncAssertSuccess(onGet -> {
            rabbitMQClient.basicPublish(publishExchange, publishRoutingKey, data,
                    onPublish -> {
                        if (!onPublish.succeeded()) {
                            Log.error(this.getClass().getSimpleName(), onPublish.cause());
                        }
                    });
        }));
        async.awaitSuccess();
    }

    private void handleServiceResponse(TestContext context, Async async, String eventBusAddress) {
        vertx.eventBus().consumer(eventBusAddress, msg -> {
            context.assertNotNull(msg.body());
            JsonObject responseJson = new JsonObject(((JsonObject) msg.body()).getString(BODY));
            checkResponse(context, responseJson);
            async.complete();
        }).exceptionHandler(event -> {
            context.fail(event.getCause());
            async.complete();
        });

    }

    /**
     * Checks the response received after a
     * {@link #publishMessageAndWaitResponse(TestContext, String, String, JsonObject)}.
     *
     * @param context      the {@link TestContext} needed to verify the correctness of the response
     * @param responseJson the response json extracted from the RabbitMQ message
     */
    protected abstract void checkResponse(TestContext context, JsonObject responseJson);

}
