package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.shared.util.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by Marco on 28/07/2017.
 */
public class RabbitMQConfig {

    private static io.vertx.rabbitmq.RabbitMQClient rabbitMQClientTest = null;
    private static io.vertx.rabbitmq.RabbitMQClient rabbitMQClient = null;
    private static RabbitMQConfig setup = null;
    private static Vertx vertx;
    private static String localhost = "localhost";

    private RabbitMQConfig() {
    }

    private RabbitMQConfig(Vertx vertx) {
        this.vertx = vertx;
    }

    public static synchronized RabbitMQConfig getInstance(Vertx vertx) {
        if (setup == null) {
            setup = new RabbitMQConfig(vertx);
            setupRabbitMQ();
            setupRabbitMQTest();
        }
        return setup;
    }

    private static void setupRabbitMQ() {
        JsonObject config = new JsonObject();
        config.put(Constants.RabbitMQ.ConfigKey.HOST, Constants.RabbitMQ.Broker.HOST);
        config.put(Constants.RabbitMQ.ConfigKey.PASSWORD, Constants.RabbitMQ.Broker.PASSWORD);
        config.put(Constants.RabbitMQ.ConfigKey.PORT, Constants.RabbitMQ.Broker.PORT);
        rabbitMQClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
    }

  private static void setupRabbitMQTest() {
        JsonObject config = new JsonObject();
        config.put(Constants.RabbitMQ.ConfigKey.HOST, localhost);
        config.put(Constants.RabbitMQ.ConfigKey.PORT, Constants.RabbitMQ.Broker.PORT);
        rabbitMQClientTest = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
    }

    public synchronized io.vertx.rabbitmq.RabbitMQClient getRabbitMQClient() 
        return rabbitMQClient;
    }

    public io.vertx.rabbitmq.RabbitMQClient getRabbitMQClientTest() {
        return rabbitMQClientTest;
    }

}
