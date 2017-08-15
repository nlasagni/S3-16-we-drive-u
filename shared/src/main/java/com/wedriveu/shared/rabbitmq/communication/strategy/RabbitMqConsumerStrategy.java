package com.wedriveu.shared.rabbitmq.communication.strategy;

import com.rabbitmq.client.ShutdownSignalException;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunicationManager;

import java.io.IOException;

/**
 * A strategy for consuming RabbitMQ messages used by
 * {@linkplain RabbitMqCommunicationManager#registerConsumer(RabbitMqConsumerStrategy, Class)}..
 *
 * @param <T> the type of the message consumed
 * @author Nicola Lasagni on 09/08/2017.
 */
public interface RabbitMqConsumerStrategy<T> {

    /**
     * Configures the queue that will receive messages.
     *
     * @param communication the {@linkplain RabbitMqCommunication} with with configuring the queue
     * @return the queue name
     * @throws IOException if something goes wrong during queue configuration
     */
    String configureQueue(RabbitMqCommunication communication) throws IOException;

    /**
     * Handles the message.
     *
     * @param message the message received
     */
    void handleMessage(T message);

    /**
     * Handles shutdown.
     *
     * @param e the exception thrown during shutdown
     */
    void handleShutdown(ShutdownSignalException e);

}
