package com.wedriveu.mobile.service.vehicle;

import com.rabbitmq.client.Channel;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.service.ServiceConsumerStrategy;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.VehicleResponse;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class EnterVehicleConsumerStrategy extends ServiceConsumerStrategy<EnterVehicleRequest> {

    private static final String TAG = EnterVehicleConsumerStrategy.class.getSimpleName();

    private User mUser;

    EnterVehicleConsumerStrategy(User user, BlockingQueue<EnterVehicleRequest> response) {
        super(TAG, response);
        mUser = user;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String userName = mUser.getUsername();
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.VEHICLE, userName);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST_ENTER_USER, userName);
        Channel channel = communication.getChannel();
        channel.queueDeclare(queue, true, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
        return queue;
    }

}
