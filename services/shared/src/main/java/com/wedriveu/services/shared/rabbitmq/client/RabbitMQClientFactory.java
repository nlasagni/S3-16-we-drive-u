package com.wedriveu.services.shared.rabbitmq.client;

import com.wedriveu.shared.util.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * Basic RabbitMQ Client Factory for requesting RabbitMQ client where needed.
 *
 * @author Marco Baldassarri
 * @since 12/08/2017.
 */
public class RabbitMQClientFactory {

    public static RabbitMQClient createClient(Vertx vertx) {
        JsonObject config = new JsonObject();
        config.put(Constants.RabbitMQ.ConfigKey.HOST, Constants.RabbitMQ.Broker.HOST);
        config.put(Constants.RabbitMQ.ConfigKey.PASSWORD, Constants.RabbitMQ.Broker.PASSWORD);
        config.put(Constants.RabbitMQ.ConfigKey.PORT, Constants.RabbitMQ.Broker.PORT);
        return RabbitMQClient.create(vertx, config);
    }

}
