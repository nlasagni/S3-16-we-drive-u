package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.utilities.Constants;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Created by Marco on 28/07/2017.
 */
public class RabbitMQConfig {

    private static final String PASSWORD = "password";
    private static io.vertx.rabbitmq.RabbitMQClient rabbitMQClient = null;
    private static RabbitMQConfig setup = null;
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String CONN_TIMEOUT = "connectionTimeout";
    private static final String REQUEST_HEARTBEAT = "requestedHeartbeat";
    private static final String HANDSHAKE_TIMEOUT = "handshakeTimeout";
    private static Vertx vertx;

    private RabbitMQConfig() { }

    private RabbitMQConfig(Vertx vertx) {
        this.vertx = vertx;
    }

    public static RabbitMQConfig getInstance(Vertx vertx) {
        if(setup == null ) {
            setup = new RabbitMQConfig(vertx);
            setupRabbitMQ();
        }
        return setup;
    }

    private static void setupRabbitMQ() {
        JsonObject config = new JsonObject();
        config.put(HOST, Constants.RABBITMQ_SERVER_HOST);
        config.put(PASSWORD, Constants.RABBITMQ_SERVER_PASSWORD);
        config.put(PORT, Constants.RABBITMQ_SERVER_PORT);
        /*config.put(CONN_TIMEOUT, Constants.RABBITMQ_SERVER_TIMEOUT);
        config.put(REQUEST_HEARTBEAT,  Constants.RABBITMQ_SERVER_TIMEOUT);
        config.put(HANDSHAKE_TIMEOUT, Constants.RABBITMQ_SERVER_TIMEOUT);*/
        rabbitMQClient = io.vertx.rabbitmq.RabbitMQClient.create(vertx, config);
    }

    public io.vertx.rabbitmq.RabbitMQClient getRabbitMQClient() {
        return rabbitMQClient;
    }

}
