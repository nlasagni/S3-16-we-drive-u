package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.utilities.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by Marco on 28/07/2017.
 */
public class RabbitMQConfig {

    private static final String PASSWORD = "password";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static io.vertx.rabbitmq.RabbitMQClient rabbitMQClient = null;
    private static RabbitMQConfig setup = null;
    private  Vertx vertx;

    private RabbitMQConfig() {
    }

    private RabbitMQConfig(Vertx vertx) {
        this.vertx = vertx;
    }

    public static RabbitMQConfig getInstance(Vertx vertx) {
       /* if (setup == null) {
            setup = new RabbitMQConfig(vertx);
            setupRabbitMQ();
        }
        return setup;*/
       RabbitMQConfig config = new RabbitMQConfig(vertx);
       config.setupRabbitMQ();
       return config;
    }

    private void setupRabbitMQ() {
        JsonObject config = new JsonObject();
        config.put(HOST, Constants.RABBITMQ_SERVER_HOST);
        config.put(PASSWORD, Constants.RABBITMQ_SERVER_PASSWORD);
        config.put(PORT, Constants.RABBITMQ_SERVER_PORT);
        rabbitMQClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
    }

    public io.vertx.rabbitmq.RabbitMQClient getRabbitMQClient() {
        return rabbitMQClient;
    }

}
