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

    private static RabbitMQClient client;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        client = RabbitMQConfig.getInstance(vertx).getRabbitMQClient();
        client.start(startFuture.completer());
    }

    protected void publish(String exchangeName, String routingKey, JsonObject data) {
        Log.info("VERTICLE PUBLISHER - PUBLISH", exchangeName + " " + routingKey + " " + data.encodePrettily());
        client.basicPublish(exchangeName, routingKey, data, onPublish -> {
        });
    }

}
