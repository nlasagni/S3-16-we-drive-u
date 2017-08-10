package com.wedriveu.mobile.service.scheduling;

import com.rabbitmq.client.Channel;
import com.wedriveu.mobile.model.User;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqCloseCommunicationStrategy;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;

/**
 * The {@linkplain SchedulingCloseCommunicationStrategy} used by the {@linkplain SchedulingServiceImpl}
 * to close a {@linkplain RabbitMqCommunication}.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
class SchedulingCloseCommunicationStrategy implements RabbitMqCloseCommunicationStrategy {

    private User mUser;

    SchedulingCloseCommunicationStrategy(User user) {
        mUser = user;
    }

    @Override
    public void closeCommunication(RabbitMqCommunication communication) throws IOException {
        String userName = mUser.getUsername();
        String queue = String.format(Constants.RabbitMQ.Queue.USER, userName);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE, userName);
        Channel channel = communication.getChannel();
        channel.queueUnbind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
    }
}
