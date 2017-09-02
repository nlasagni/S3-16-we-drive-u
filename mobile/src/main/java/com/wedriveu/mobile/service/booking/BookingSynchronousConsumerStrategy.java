package com.wedriveu.mobile.service.booking;

import com.rabbitmq.client.Channel;
import com.wedriveu.mobile.service.ServiceSynchronousConsumerStrategy;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.message.CreateBookingResponse;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

/**
 * The {@linkplain ServiceSynchronousConsumerStrategy} to manage a {@linkplain CreateBookingResponse}.
 * @author Nicola Lasagni on 19/08/2017.
 */
public class BookingSynchronousConsumerStrategy extends ServiceSynchronousConsumerStrategy<CreateBookingResponse> {

    private String mUsername;

    /**
     * Instantiates a new BookingSynchronousConsumerStrategy.
     *
     * @param username the username that made the request
     * @param response the {@linkplain CreateBookingResponse} received
     */
    BookingSynchronousConsumerStrategy(String username, BlockingQueue<CreateBookingResponse> response) {
        super(response);
        mUsername = username;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String randomId = UUID.randomUUID().toString();
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.BOOKING, randomId);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.CREATE_BOOKING_RESPONSE, mUsername);
        Channel channel = communication.getChannel();
        channel.queueDeclare(queue, true, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.BOOKING, routingKey);
        return queue;
    }

}
