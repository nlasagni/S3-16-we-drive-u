package com.wedriveu.shared.rabbitmq.communication.strategy;

import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunicationManager;

import java.io.IOException;

/**
 * A strategy to do additional communication closing operation (for example deleting queues etc.).
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
public interface RabbitMqCloseCommunicationStrategy {

    /**
     * Close communication behaviour used by
     * {@linkplain RabbitMqCommunicationManager#closeCommunication(RabbitMqCloseCommunicationStrategy)}.
     *
     * @param communication the communication
     * @throws IOException if something goes wrong during the additional closing operation
     */
    void closeCommunication(RabbitMqCommunication communication) throws IOException;

}
