package com.wedriveu.services.shared.rabbitmq.client;

import com.wedriveu.services.shared.utilities.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.rabbitmq.RabbitMQClient;

/**
 * Created by Marco on 28/07/2017.
 */
public class RabbitMQFactory {

    private static final String PASSWORD = "password";
    private static final String HOST = "host";
    private static final String PORT = "port";

    public static RabbitMQClient createClient(Vertx vertx) {
        return setupRabbitMQ(vertx);
    }

    private static RabbitMQClient setupRabbitMQ(Vertx vertx) {
        JsonObject config = new JsonObject();
        config.put(HOST, Constants.RABBITMQ_SERVER_HOST);
        config.put(PASSWORD, Constants.RABBITMQ_SERVER_PASSWORD);
        config.put(PORT, Constants.RABBITMQ_SERVER_PORT);
        return io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
    }

}
