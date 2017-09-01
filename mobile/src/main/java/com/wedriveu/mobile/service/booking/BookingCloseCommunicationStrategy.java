package com.wedriveu.mobile.service.booking;

import com.rabbitmq.client.Channel;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqCloseCommunicationStrategy;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;

/**
 * The {@linkplain RabbitMqCloseCommunicationStrategy} used by the {@linkplain BookingServiceImpl}
 * to close a {@linkplain RabbitMqCommunication}.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
public class BookingCloseCommunicationStrategy implements RabbitMqCloseCommunicationStrategy {

    private String mUsername;

    BookingCloseCommunicationStrategy(String username) {
        mUsername = username;
    }

    @Override
    public void closeCommunication(RabbitMqCommunication communication) throws IOException {
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.BOOKING, mUsername);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.CREATE_BOOKING_RESPONSE, mUsername);
        Channel channel = communication.getChannel();
        channel.queueUnbind(queue, Constants.RabbitMQ.Exchanges.BOOKING, routingKey);
        channel.queueDelete(queue);
    }

}
