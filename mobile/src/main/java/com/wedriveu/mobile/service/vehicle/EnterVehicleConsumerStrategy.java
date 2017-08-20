package com.wedriveu.mobile.service.vehicle;

import android.app.Activity;
import android.util.Log;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
class EnterVehicleConsumerStrategy implements RabbitMqConsumerStrategy<EnterVehicleRequest> {

    private static final String TAG = EnterVehicleConsumerStrategy.class.getSimpleName();
    private static final String ENTER_ERROR = "Error occurred while confirming enter operation.";

    private Activity mActivity;
    private User mUser;
    private ServiceOperationCallback<EnterVehicleRequest> mCallback;

    EnterVehicleConsumerStrategy(Activity activity,
                                 User user,
                                 ServiceOperationCallback<EnterVehicleRequest> callback) {
        mActivity = activity;
        mUser = user;
        mCallback = callback;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String userName = mUser.getUsername();
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.ENTER_VEHICLE, userName);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST_ENTER_USER, userName);
        Channel channel = communication.getChannel();
        channel.queueDeclare(queue, true, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
        return queue;
    }

    @Override
    public void handleMessage(final EnterVehicleRequest message) {
        //TODO
        Log.i(this.getClass().getSimpleName(), "Enter: " + message.getLicensePlate());
        if (message == null) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onServiceOperationFinished(new ServiceResult<>(message, ENTER_ERROR));
            }
        });
    }

    @Override
    public void handleShutdown(ShutdownSignalException e) {
        Log.e(TAG, e.getLocalizedMessage(), e);
    }

}
