package com.wedriveu.services.shared.rabbitmq;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by Marco on 28/07/2017.
 */
public class RabbitMQClient {

    private static io.vertx.rabbitmq.RabbitMQClient rabbitMQClient = null;
    private static RabbitMQClient setup = null;

    public static RabbitMQClient getInstance() {
        if(setup == null ) {
            setup = new RabbitMQClient();
            setupRabbitMQ();
        }
        return setup;
    }

    private static void setupRabbitMQ() {
        JsonObject config = new JsonObject();
        config.put("user", "user1");
        config.put("password", "password1");
        config.put("host", "localhost");
        config.put("port", 5672);
        config.put("virtualHost", "vhost1");
        config.put("connectionTimeout", 60); // in seconds
        config.put("requestedHeartbeat", 60); // in seconds
        config.put("handshakeTimeout", 60); // in seconds
        config.put("requestedChannelMax", 5);
        config.put("networkRecoveryInterval", 5); // in seconds
        config.put("automaticRecoveryEnabled", true);
        Vertx vertx = Vertx.vertx();
        rabbitMQClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
    }

    public io.vertx.rabbitmq.RabbitMQClient getRabbitMQClient() {
        return rabbitMQClient;
    }

}
