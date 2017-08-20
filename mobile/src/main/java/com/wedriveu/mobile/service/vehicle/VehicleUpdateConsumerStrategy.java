package com.wedriveu.mobile.service.vehicle;

import android.app.Activity;
import android.util.Log;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.store.VehicleStore;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;

/**
 * @author Nicola Lasagni on 20/08/2017.
 */
class VehicleUpdateConsumerStrategy implements RabbitMqConsumerStrategy<UpdateToService> {

    private static final String TAG = EnterVehicleConsumerStrategy.class.getSimpleName();
    private static final String UPDATE_ERROR = "Error occurred while updating vehicle position.";

    private Activity mActivity;
    private User mUser;
    private VehicleStore mVehicleStore;
    private ServiceOperationCallback<Vehicle> mCallback;

    VehicleUpdateConsumerStrategy(Activity activity,
                                  User user,
                                  VehicleStore vehicleStore,
                                  ServiceOperationCallback<Vehicle> callback) {
        mActivity = activity;
        mUser = user;
        mVehicleStore = vehicleStore;
        mCallback = callback;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String userName = mUser.getUsername();
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.VEHICLE_UPDATE, userName);
        String routingKey = Constants.RabbitMQ.RoutingKey.VEHICLE_UPDATE;
        Channel channel = communication.getChannel();
        channel.queueDeclare(queue, true, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
        return queue;
    }

    @Override
    public void handleMessage(final UpdateToService message) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String error = null;
                Vehicle vehicle = mVehicleStore.getVehicle();
                if (message == null) {
                    error = UPDATE_ERROR;
                } else if (!vehicle.getLicencePlate().equals(message.getLicense())) {
                    return;
                } else {
                    vehicle.setPosition(message.getPosition());
                    mVehicleStore.storeVehicle(vehicle);
                }
                mCallback.onServiceOperationFinished(new ServiceResult<>(vehicle, error));
            }
        });
    }

    @Override
    public void handleShutdown(ShutdownSignalException e) {
        Log.e(TAG, e.getLocalizedMessage(), e);
    }
}
