package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * Basic Vert.x RabbitMQ Publisher Verticle.
 *
 * @author Marco Baldassarri
 */
public class VerticlePublisher extends AbstractVerticle {

    private RabbitMQClient client;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        client = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
        client.start(startFuture.completer());
    }

    protected void publish(String exchangeName, String routingKey, JsonObject data) {
        Log.info(VerticlePublisher.class.getSimpleName(), "publish message on " + exchangeName + " " + routingKey + " " + data.toString());
        client.basicPublish(exchangeName, routingKey, data, onPublish -> {
            if (!onPublish.succeeded()) {
                Log.error(VerticlePublisher.class.getSimpleName(), onPublish.cause().getMessage(), onPublish.cause());
            }
        });
    }

}
