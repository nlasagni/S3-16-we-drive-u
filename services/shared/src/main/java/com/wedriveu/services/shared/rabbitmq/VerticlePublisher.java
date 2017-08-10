package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;
import static com.wedriveu.services.shared.utilities.Constants.EXCHANGE_TYPE;
import static com.wedriveu.services.shared.utilities.Constants.VEHICLE_SERVICE_EXCHANGE;

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

    public static void startAndDeclareExchange(Vertx vertx, String exchangeName) {
        client = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
        client.start(started -> {
            declareExchange(exchangeName, onDeclareCompleted -> {
                if (!onDeclareCompleted.succeeded()) {
                    Log.error(TAG, onDeclareCompleted.cause().getMessage(), onDeclareCompleted.cause());
                }
            });
        });
    }

    private static void declareExchange(String exchangeName, Handler<AsyncResult<Void>> handler) {
        client.exchangeDeclare(exchangeName, EXCHANGE_TYPE, false, false, handler);
    }

    protected void publish(String exchangeName, String routingKey, JsonObject data) {
        client.basicPublish(exchangeName, routingKey, data, null);
    }

}
