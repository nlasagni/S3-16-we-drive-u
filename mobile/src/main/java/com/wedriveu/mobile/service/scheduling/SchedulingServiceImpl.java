package com.wedriveu.mobile.service.scheduling;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.rabbitmq.client.ExceptionHandler;
import com.wedriveu.mobile.model.SchedulingLocation;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceExceptionHandler;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.shared.entity.Position;
import com.wedriveu.shared.entity.VehicleRequest;
import com.wedriveu.shared.entity.VehicleResponse;
import com.wedriveu.shared.rabbitmq.communication.DefaultRabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.config.RabbitMqCommunicationConfig;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqCloseCommunicationStrategy;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    private Activity mActivity;
    private UserStore mUserStore;
    private Double mUserLatitude;
    private Double mUserLongitude;
    private SchedulingLocation schedulingLocation;
    private RabbitMqCommunicationManager mCommunicationManager;

    public SchedulingServiceImpl(Activity activity, UserStore userStore) {
        mActivity = activity;
        mUserStore = userStore;
        schedulingLocation = new SchedulingLocation();
        mCommunicationManager = new DefaultRabbitMqCommunicationManager();
    }

    @Override
    public void findNearestVehicle(final Place address, final ServiceOperationCallback<Vehicle> callback) {
        schedulingLocation.setDestinationLatitude(address.getLatLng().latitude);
        schedulingLocation.setDestinationLongitude(address.getLatLng().longitude);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ServiceResult<Vehicle> result;
                try {
                    ExceptionHandler exceptionHandler = new ServiceExceptionHandler(mActivity, callback);
                    RabbitMqCommunicationConfig config =
                            new RabbitMqCommunicationConfig.Builder()
                                    .host(Constants.RabbitMQ.Broker.HOST)
                                    .password(Constants.RabbitMQ.Broker.PASSWORD)
                                    .exceptionHandler(exceptionHandler).build();
                    mCommunicationManager.setUpCommunication(config);
                    VehicleRequest request = createRequest(address);
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

    @Override
    public void onLocationAvailable(Location location) {
        mUserLatitude = location.getLatitude();
        mUserLongitude = location.getLongitude();
    }

    @Override
    public void onLocationServiceDisabled() {
        mUserLatitude = null;
        mUserLongitude = null;
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

    private VehicleRequest createRequest(Place destinationAddress) throws UnsupportedEncodingException {
        VehicleRequest request = new VehicleRequest();
        request.setUsername(mUserStore.getUser().getUsername());
        Position userPosition = new Position(mUserLatitude, mUserLongitude);
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
            } else if (!TextUtils.isEmpty(response.getLicencePlate())) {
                    vehicle = new Vehicle(response.getLicencePlate(),
                            response.getVehicleName(),
                            response.getDescription(),
                            response.getPictureURL(),
                            response.getArriveAtUserTime(),
                            response.getArriveAtDestinationTime());
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
