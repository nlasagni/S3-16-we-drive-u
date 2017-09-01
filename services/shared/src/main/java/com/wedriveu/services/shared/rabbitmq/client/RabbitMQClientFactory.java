package com.wedriveu.services.shared.rabbitmq.client;

import com.wedriveu.shared.util.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * Basic RabbitMQ Client Factory for requesting RabbitMQ client where needed.
 *
 * @author Marco Baldassarri
 */
public class RabbitMQClientFactory {

    /**
     * creates a new rabbitMQ client with the given vert.x instance
     *
     * @param vertx the vert.x instance
     *
     * @return a new rabbitmq client
     */
    public static RabbitMQClient createClient(Vertx vertx) {
        return RabbitMQClient.create(vertx, createClientConfig());
    }

    /**
     * creates the configuration of the client
     *
     * @return a json object with the configuration of the client
     */
    public static JsonObject createClientConfig() {
        JsonObject config = new JsonObject();
        config.put(Constants.RabbitMQ.ConfigKey.HOST, Constants.RabbitMQ.Broker.HOST);
        config.put(Constants.RabbitMQ.ConfigKey.PASSWORD, Constants.RabbitMQ.Broker.PASSWORD);
        config.put(Constants.RabbitMQ.ConfigKey.PORT, Constants.RabbitMQ.Broker.PORT);
        return config;
    }

}
