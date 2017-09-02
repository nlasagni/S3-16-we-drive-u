package com.wedriveu.mobile.service.vehicle;

import android.os.Message;
import android.util.Log;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import com.wedriveu.mobile.service.ServiceOperationHandler;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.UUID;

/**
 * The {@linkplain RabbitMqConsumerStrategy} to manage a {@linkplain VehicleUpdate}.
 *
 * @author Nicola Lasagni on 20/08/2017.
 */
class VehicleUpdateConsumerStrategy implements RabbitMqConsumerStrategy<VehicleUpdate> {

    private static final String TAG = EnterVehicleConsumerStrategy.class.getSimpleName();
    private static final String UPDATE_ERROR = "Error occurred while updating vehicle position.";

    private ServiceOperationHandler<?, VehicleUpdate> mHandler;

    /**
     * Instantiates a new VehicleUpdateConsumerStrategy.
     *
     * @param handler  the handler of the {@linkplain ServiceResult}
     */
    VehicleUpdateConsumerStrategy(ServiceOperationHandler<?, VehicleUpdate> handler) {
        mHandler = handler;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String randomId = UUID.randomUUID().toString();
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.VEHICLE_UPDATE, randomId);
        String routingKey = Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE;
        Channel channel = communication.getChannel();
        channel.queueDeclare(queue, true, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
        return queue;
    }

    @Override
    public void handleMessage(final VehicleUpdate update) {
        Message message = mHandler.obtainMessage();
        message.obj = new ServiceResult<>(update, update == null ? UPDATE_ERROR : null);
        mHandler.sendMessage(message);
    }

    @Override
    public void handleShutdown(ShutdownSignalException e) {
        Log.e(TAG, e.getLocalizedMessage(), e);
    }
}
