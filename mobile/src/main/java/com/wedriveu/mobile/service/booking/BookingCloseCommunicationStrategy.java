package com.wedriveu.mobile.service.booking;

import com.rabbitmq.client.Channel;
import com.wedriveu.mobile.model.User;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqCloseCommunicationStrategy;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class BookingCloseCommunicationStrategy implements RabbitMqCloseCommunicationStrategy {

    private User mUser;

    BookingCloseCommunicationStrategy(User user) {
        mUser = user;
    }

    @Override
    public void closeCommunication(RabbitMqCommunication communication) throws IOException {
        String userName = mUser.getUsername();
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.BOOKING, userName);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.CREATE_BOOKING_RESPONSE, userName);
        Channel channel = communication.getChannel();
        channel.queueUnbind(queue, Constants.RabbitMQ.Exchanges.BOOKING, routingKey);
        channel.queueDelete(queue);
    }

}
