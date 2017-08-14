package com.wedriveu.shared.rabbitmq.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.wedriveu.shared.rabbitmq.communication.config.RabbitMqCommunicationConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Factory for creating {@linkplain RabbitMqCommunication}.
 *
 * @author Nicola Lasagni on 10/08/2017.
 */
class RabbitMqCommunicationFactory {

    private static RabbitMqCommunicationFactory instance;

    private RabbitMqCommunicationFactory() {}

    /**
     * Gets a {@linkplain RabbitMqCommunicationFactory} instance.
     *
     * @return the instance of the {@linkplain RabbitMqCommunicationFactory}
     */
    static RabbitMqCommunicationFactory getInstance() {
        if (instance == null) {
            instance = new RabbitMqCommunicationFactory();
        }
        return instance;
    }

    /**
     * Create a {@linkplain RabbitMqCommunication} by using the provided {@linkplain RabbitMqCommunicationConfig}.
     *
     * @param config the configurations provided with which create the {@linkplain RabbitMqCommunication}
     * @return a new {@linkplain RabbitMqCommunication}
     * @throws IOException      if something goes wrong during the {@linkplain RabbitMqCommunication} creation
     * @throws TimeoutException if something goes wrong during the {@linkplain RabbitMqCommunication} creation
     */
    RabbitMqCommunication createCommunication(RabbitMqCommunicationConfig config) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(config.getHost());
        connectionFactory.setPassword(config.getPassword());
        connectionFactory.setExceptionHandler(config.getExceptionHandler());
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        return new RabbitMqCommunication(connection, channel);
    }

}
