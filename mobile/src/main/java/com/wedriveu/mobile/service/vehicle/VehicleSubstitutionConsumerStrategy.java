package com.wedriveu.mobile.service.vehicle;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.util.Dates;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.VehicleResponse;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * The {@linkplain RabbitMqConsumerStrategy} to manage a {@linkplain VehicleResponse}.
 * 
 * @author Nicola Lasagni on 28/08/2017.
 */
public class VehicleSubstitutionConsumerStrategy implements RabbitMqConsumerStrategy<VehicleResponse> {

    private static final String TAG = EnterVehicleConsumerStrategy.class.getSimpleName();
    private static final String SUBSTITUTION_ERROR = "Error occurred while substituting vehicle position.";

    private String mUsername;
    private Handler mHandler;

    /**
     * Instantiates a new VehicleSubstitutionConsumerStrategy.
     *
     * @param username the username used to subscribe to vehicle substitution events
     * @param handler  the handler of the {@linkplain ServiceResult}
     */
    VehicleSubstitutionConsumerStrategy(String username, Handler handler) {
        mUsername = username;
        mHandler = handler;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        String randomId = UUID.randomUUID().toString();
        String queue = String.format(com.wedriveu.mobile.util.Constants.Queue.VEHICLE_SUBSTITUTION, randomId);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_SUBSTITUTION, mUsername);
        Channel channel = communication.getChannel();
        channel.queueDeclare(queue, true, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
        return queue;
    }

    @Override
    public void handleMessage(final VehicleResponse response) {
        Vehicle vehicle = null;
        String error = "";
        if (response == null) {
            error = SUBSTITUTION_ERROR;
        } else if (!TextUtils.isEmpty(response.getLicensePlate())) {
            Date arriveAtUserTime = new Date(response.getArriveAtUserTime());
            Date arriveAtDestinationTime = new Date(response.getArriveAtDestinationTime());
            vehicle = new Vehicle(response.getLicensePlate(),
                    null,
                    response.getVehicleName(),
                    response.getDescription(),
                    response.getPictureURL(),
                    Dates.format(arriveAtUserTime),
                    Dates.format(arriveAtDestinationTime));
        } else if (!TextUtils.isEmpty(response.getNotEligibleVehicleFound())) {
            error = response.getNotEligibleVehicleFound();
        } else {
            error = SUBSTITUTION_ERROR;
        }
        Message message = mHandler.obtainMessage();
        message.obj = new ServiceResult<>(vehicle, error);
        mHandler.sendMessage(message);
    }

    @Override
    public void handleShutdown(ShutdownSignalException e) {
        Log.e(TAG, e.getLocalizedMessage(), e);
    }

}
