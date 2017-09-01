package com.wedriveu.mobile.service.scheduling;

import android.os.AsyncTask;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.rabbitmq.client.ExceptionHandler;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceExceptionHandler;
import com.wedriveu.mobile.service.ServiceOperationHandler;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.util.Dates;
import com.wedriveu.shared.rabbitmq.communication.DefaultRabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.config.RabbitMqCommunicationConfig;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqCloseCommunicationStrategy;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.VehicleRequest;
import com.wedriveu.shared.rabbitmq.message.VehicleResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Position;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.mobile.util.Constants.CLOSE_COMMUNICATION_ERROR;
import static com.wedriveu.mobile.util.Constants.NO_RESPONSE_DATA_ERROR;

/**
 * The effective {@linkplain SchedulingService} implementation
 *
 * @author Marco Baldassarri on 18/07/2017.
 * @author Nicola Lasagni on 09/08/2017.
 */
public class SchedulingServiceImpl implements SchedulingService {

    private static final String TAG = SchedulingServiceImpl.class.getSimpleName();
    private static final String SCHEDULING_ERROR = "Error occurred while performing scheduling operation.";

    private RabbitMqCommunicationManager mCommunicationManager;
    private String mConsumerTag;

    /**
     * Instantiates a new SchedulingService.
     */
    public SchedulingServiceImpl() {
        mCommunicationManager = new DefaultRabbitMqCommunicationManager();
    }

    @Override
    public <T> void findNearestVehicle(final String username,
                                   final Position userPosition,
                                   final Place address,
                                   final ServiceOperationHandler<T, Vehicle> handler) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ServiceResult<Vehicle> result;
                try {
                    ExceptionHandler exceptionHandler = new ServiceExceptionHandler();
                    RabbitMqCommunicationConfig config =
                            new RabbitMqCommunicationConfig.Builder()
                                    .host(Constants.RabbitMQ.Broker.HOST)
                                    .password(Constants.RabbitMQ.Broker.PASSWORD)
                                    .exceptionHandler(exceptionHandler).build();
                    mCommunicationManager.setUpCommunication(config);
                    VehicleRequest request = createRequest(username, userPosition, address);
                    sendRequest(request);
                    final BlockingQueue<VehicleResponse> response = new ArrayBlockingQueue<>(1);
                    VehicleResponse responseBody = subscribeForResponse(username, response);
                    result = createServiceResult(responseBody);
                    closeCommunication(username);
                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e(TAG, SCHEDULING_ERROR, e);
                    result = new ServiceResult<>(null, SCHEDULING_ERROR);
                }
                Message message = handler.obtainMessage();
                message.obj = result;
                handler.sendMessage(message);
                return null;
            }
        }.execute();
    }

    private void closeCommunication(String username) {
        try {
            mCommunicationManager.unregisterConsumer(mConsumerTag);
            RabbitMqCloseCommunicationStrategy strategy =
                    new SchedulingCloseCommunicationStrategy(username);
            mCommunicationManager.closeCommunication(strategy);
        } catch (IOException | TimeoutException e) {
            Log.e(TAG, CLOSE_COMMUNICATION_ERROR, e);
        }
    }

    private VehicleRequest createRequest(String username,
                                         Position userPosition,
                                         Place destinationAddress) throws UnsupportedEncodingException {
        VehicleRequest request = new VehicleRequest();
        request.setUsername(username);
        LatLng coordinates = destinationAddress.getLatLng();
        Position destinationPosition = new Position(coordinates.latitude, coordinates.longitude);
        request.setUserPosition(userPosition);
        request.setDestinationPosition(destinationPosition);
        return request;
    }

    private void sendRequest(VehicleRequest request) throws IOException {
        mCommunicationManager.publishMessage(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST,
                request);
    }

    private VehicleResponse subscribeForResponse(String username,
                                                 final BlockingQueue<VehicleResponse> responseBlockingQueue)
            throws IOException, InterruptedException {
        RabbitMqConsumerStrategy<VehicleResponse> strategy =
                new SchedulingSynchronousConsumerStrategy(username, responseBlockingQueue);
        mConsumerTag = mCommunicationManager.registerConsumer(strategy, VehicleResponse.class);
        return responseBlockingQueue.poll(com.wedriveu.mobile.util.Constants.SERVICE_OPERATION_TIMEOUT,
                TimeUnit.MILLISECONDS);

    }

    private ServiceResult<Vehicle> createServiceResult(VehicleResponse response) throws IOException {
            Vehicle vehicle = null;
            String error = "";
            if (response == null) {
                error = NO_RESPONSE_DATA_ERROR;
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
                error = NO_RESPONSE_DATA_ERROR;
            }
        return new ServiceResult<>(vehicle, error);
    }

}
