package com.wedriveu.mobile.service.scheduling;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.rabbitmq.client.ExceptionHandler;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceExceptionHandler;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.store.UserStore;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.mobile.util.Constants.CLOSE_COMMUNICATION_ERROR;
import static com.wedriveu.mobile.util.Constants.NO_RESPONSE_DATA_ERROR;

/**
 * @author Marco Baldassarri on 18/07/2017.
 * @author Nicola Lasagni on 09/08/2017.
 */
public class SchedulingServiceImpl implements SchedulingService {

    private static final String TAG = SchedulingServiceImpl.class.getSimpleName();
    private static final String SCHEDULING_ERROR = "Error occurred while performing scheduling operation.";
    private static final String FORMAT = "dd/MM/yyyy HH:mm";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(FORMAT, Locale.getDefault());

    private Activity mActivity;
    private UserStore mUserStore;
    private RabbitMqCommunicationManager mCommunicationManager;

    public SchedulingServiceImpl(Activity activity, UserStore userStore) {
        mActivity = activity;
        mUserStore = userStore;
        mCommunicationManager = new DefaultRabbitMqCommunicationManager();
    }

    @Override
    public void findNearestVehicle(final Position userPosition,
                                   final Place address,
                                   final ServiceOperationCallback<Vehicle> callback) {
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
                    VehicleRequest request = createRequest(userPosition, address);
                    sendRequest(request);
                    final BlockingQueue<VehicleResponse> response = new ArrayBlockingQueue<>(1);
                    VehicleResponse responseBody = subscribeForResponse(response);
                    result = createServiceResult(responseBody);
                    closeCommunication();
                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e(TAG, SCHEDULING_ERROR, e);
                    result = new ServiceResult<>(null, SCHEDULING_ERROR);
                }
                handleResponse(result, callback);
                return null;
            }
        }.execute();
    }

    private void closeCommunication() {
        try {
            RabbitMqCloseCommunicationStrategy strategy =
                    new SchedulingCloseCommunicationStrategy(mUserStore.getUser());
            mCommunicationManager.closeCommunication(strategy);
        } catch (IOException | TimeoutException e) {
            Log.e(TAG, CLOSE_COMMUNICATION_ERROR, e);
        }
    }

    private VehicleRequest createRequest(Position userPosition,
                                         Place destinationAddress) throws UnsupportedEncodingException {
        VehicleRequest request = new VehicleRequest();
        request.setUsername(mUserStore.getUser().getUsername());
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

    private VehicleResponse subscribeForResponse(final BlockingQueue<VehicleResponse> responseBlockingQueue)
            throws IOException, InterruptedException {
        RabbitMqConsumerStrategy<VehicleResponse> strategy =
                new SchedulingConsumerStrategy(mUserStore.getUser(), responseBlockingQueue);
        mCommunicationManager.registerConsumer(strategy, VehicleResponse.class);
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
                        response.getVehicleName(),
                        response.getDescription(),
                        response.getPictureURL(),
                        DATE_FORMAT.format(arriveAtUserTime),
                        DATE_FORMAT.format(arriveAtDestinationTime));
            } else if (!TextUtils.isEmpty(response.getNotEligibleVehicleFound())) {
                error = response.getNotEligibleVehicleFound();
            } else {
                error = NO_RESPONSE_DATA_ERROR;
            }
        return new ServiceResult<>(vehicle, error);
    }

    private void handleResponse(final ServiceResult<Vehicle> result,
                                final ServiceOperationCallback<Vehicle> callback) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onServiceOperationFinished(result);
            }
        });
    }

}
