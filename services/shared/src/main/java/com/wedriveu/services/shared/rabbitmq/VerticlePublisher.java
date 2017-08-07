package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.services.shared.utilities.Constants.EXCHANGE_TYPE;
import static com.wedriveu.services.shared.utilities.Constants.VEHICLE_SERVICE_EXCHANGE;

/**
 * Created by Marco on 07/08/2017.
 */
public class VerticlePublisher extends AbstractVerticle {

    private static RabbitMQClient client;
    private static String TAG = VerticlePublisher.class.getSimpleName();

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        startFuture.complete();
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
        client.exchangeDeclare(VEHICLE_SERVICE_EXCHANGE,
                EXCHANGE_TYPE,
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
