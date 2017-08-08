package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * Basic Vert.x RabbitMQ Publisher Verticle.
 *
 * @author Marco Baldassarri
 */
public class VerticlePublisher extends AbstractVerticle {

    private static RabbitMQClient client;
    private static String TAG = VerticlePublisher.class.getSimpleName();

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
    }

    protected void publish(String exchangeName, String routingKey, JsonObject data) {
        client = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
        client.start(onStartCompleted -> {
                    if (onStartCompleted.succeeded()) {
                        declareExchanges(onDeclareCompleted -> {
                            if (onDeclareCompleted.succeeded()) {
                                publishToConsumer(exchangeName, routingKey, data);
                            } else {
                                Log.error(TAG, onDeclareCompleted.cause().getMessage(), onDeclareCompleted.cause());
                            }
                        });
                    }
                }
        );
    }

    protected void declareExchanges(Handler<AsyncResult<Void>> handler) {
        client.exchangeDeclare(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.Exchanges.Type.DIRECT,
                false,
                false,
                handler);
    }

    protected void publishToConsumer(String exchangeName,
                                     String routingKey,
                                     JsonObject dataToUser) {
        client.basicPublish(exchangeName, routingKey, dataToUser, null);
    }

}
