package com.wedriveu.mobile.service.login;

import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqCloseCommunicationStrategy;

import java.io.IOException;

/**
 * The {@linkplain RabbitMqCloseCommunicationStrategy} used by the {@linkplain LoginServiceImpl}
 * to close a {@linkplain RabbitMqCommunication}.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
class LoginCloseCommunicationStrategy implements RabbitMqCloseCommunicationStrategy {

    private String mRequestId;

    LoginCloseCommunicationStrategy(String requestId) {
        mRequestId = requestId;
    }

    @Override
    public void closeCommunication(RabbitMqCommunication communication) throws IOException {
        communication.getChannel().queueDelete(mRequestId);
    }

}
