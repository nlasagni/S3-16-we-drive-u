package com.wedriveu.mobile.service.scheduling;

import com.rabbitmq.client.Channel;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.service.ServiceConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.VehicleResponse;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * @author Nicola Lasagni on 09/08/2017.
 */
class SchedulingConsumerStrategy extends ServiceConsumerStrategy<VehicleResponse> {

    private static final String TAG = SchedulingConsumerStrategy.class.getSimpleName();

    private User mUser;

    SchedulingConsumerStrategy(User user, BlockingQueue<VehicleResponse> response) {
        super(TAG, response);
        mUser = user;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String userName = mUser.getUsername();
        String queue = String.format(Constants.RabbitMQ.Queue.USER, userName);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE, userName);
        String userQueue = String.format(Constants.RabbitMQ.Queue.USER, userName);
        Channel channel = communication.getChannel();
        channel.queueDeclare(userQueue, true, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
        return queue;
    }

}
