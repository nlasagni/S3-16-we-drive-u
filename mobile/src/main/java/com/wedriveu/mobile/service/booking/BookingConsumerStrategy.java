package com.wedriveu.mobile.service.booking;

import com.rabbitmq.client.Channel;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.service.ServiceConsumerStrategy;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.message.CreateBookingResponse;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class BookingConsumerStrategy extends ServiceConsumerStrategy<CreateBookingResponse> {

    private static final String TAG = BookingConsumerStrategy.class.getSimpleName();

    private User mUser;

    BookingConsumerStrategy(User user, BlockingQueue<CreateBookingResponse> response) {
        super(TAG, response);
        mUser = user;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String userName = mUser.getUsername();
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.BOOKING, userName);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.CREATE_BOOKING_RESPONSE, userName);
        String userQueue = String.format(com.wedriveu.mobile.util.Constants.Queue.USER, userName);
        Channel channel = communication.getChannel();
        channel.queueDeclare(userQueue, false, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.BOOKING, routingKey);
        return queue;
    }

}
