package com.wedriveu.mobile.service.scheduling;

import com.rabbitmq.client.Channel;
import com.wedriveu.mobile.service.ServiceSynchronousConsumerStrategy;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.message.VehicleResponse;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * The {@linkplain ServiceSynchronousConsumerStrategy} to manage a {@linkplain VehicleResponse}.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
class SchedulingSynchronousConsumerStrategy extends ServiceSynchronousConsumerStrategy<VehicleResponse> {

    private String mUsername;

    SchedulingSynchronousConsumerStrategy(String username, BlockingQueue<VehicleResponse> response) {
        super(response);
        mUsername = username;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.USER, mUsername);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE, mUsername);
        Channel channel = communication.getChannel();
        channel.queueDeclare(queue, true, false, false, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
        return queue;
    }

}
