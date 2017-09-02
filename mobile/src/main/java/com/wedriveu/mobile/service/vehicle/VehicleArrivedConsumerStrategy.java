package com.wedriveu.mobile.service.vehicle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.CompleteBookingResponse;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.UUID;

/**
 * The {@linkplain RabbitMqConsumerStrategy} to manage a {@linkplain CompleteBookingResponse}.
 *
 * @author Nicola Lasagni on 20/08/2017.
 */
public class VehicleArrivedConsumerStrategy implements RabbitMqConsumerStrategy<CompleteBookingResponse> {

    private static final String TAG = EnterVehicleConsumerStrategy.class.getSimpleName();
    private static final String ARRIVED_ERROR = "Error occurred while vehicle arrived.";

    private String mUsername;
    private Handler mHandler;

    /**
     * Instantiates a new VehicleArrivedConsumerStrategy.
     *
     * @param username the username used to subscribe to vehicle arrived events
     * @param handler  the handler of the {@linkplain ServiceResult}
     */
    VehicleArrivedConsumerStrategy(String username,
                                   Handler handler) {
        mUsername = username;
        mHandler = handler;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String randomId = UUID.randomUUID().toString();
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.VEHICLE_ARRIVED, randomId);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.COMPLETE_BOOKING_RESPONSE_USER, mUsername);
        Channel channel = communication.getChannel();
        channel.queueDeclare(queue, true, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.BOOKING, routingKey);
        return queue;
    }

    @Override
    public void handleMessage(final CompleteBookingResponse response) {
        Message message = mHandler.obtainMessage();
        message.obj = new ServiceResult<>(response, ARRIVED_ERROR);
        mHandler.sendMessage(message);
    }

    @Override
    public void handleShutdown(ShutdownSignalException e) {
        Log.e(TAG, e.getLocalizedMessage(), e);
    }

}
