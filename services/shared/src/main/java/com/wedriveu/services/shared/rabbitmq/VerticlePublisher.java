package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
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

    private RabbitMQClient client;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        client = RabbitMQClientFactory.createClient(vertx);
        client.start(startFuture.completer());
    }

    protected void publish(String exchangeName,
                           String routingKey,
                           JsonObject data,
                           Handler<AsyncResult<Void>> onPublish) {
        client.basicPublish(exchangeName, routingKey, data, onPublish);
    }

}
