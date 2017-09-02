package com.wedriveu.mobile.service.vehicle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.UUID;

/**
 * The {@linkplain RabbitMqConsumerStrategy} to manage a {@linkplain EnterVehicleRequest}.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
class EnterVehicleConsumerStrategy implements RabbitMqConsumerStrategy<EnterVehicleRequest> {

    private static final String TAG = EnterVehicleConsumerStrategy.class.getSimpleName();
    private static final String ENTER_ERROR = "Error occurred while confirming enter operation.";

    private String mUsername;
    private Handler mHandler;

    /**
     * Instantiates a new EnterVehicleConsumerStrategy.
     *
     * @param username the username used to enter into the vehicle
     * @param handler  the handler of the {@linkplain ServiceResult}
     */
    EnterVehicleConsumerStrategy(String username, Handler handler) {
        mUsername = username;
        mHandler = handler;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String randomId = UUID.randomUUID().toString();
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.ENTER_VEHICLE, randomId);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST_ENTER_USER, mUsername);
        Channel channel = communication.getChannel();
        channel.queueDeclare(queue, true, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
        return queue;
    }

    @Override
    public void handleMessage(final EnterVehicleRequest request) {
        if (request == null) {
            return;
        }
        Message message = mHandler.obtainMessage();
        message.obj = new ServiceResult<>(request, ENTER_ERROR);
        mHandler.handleMessage(message);
    }

    @Override
    public void handleShutdown(ShutdownSignalException e) {
        Log.e(TAG, e.getLocalizedMessage(), e);
    }

}
