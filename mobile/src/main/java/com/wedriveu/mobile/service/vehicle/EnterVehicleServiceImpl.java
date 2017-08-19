package com.wedriveu.mobile.service.vehicle;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.rabbitmq.client.ExceptionHandler;
import com.wedriveu.mobile.service.ServiceExceptionHandler;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.store.BookingStore;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.shared.rabbitmq.communication.DefaultRabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.config.RabbitMqCommunicationConfig;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleRequest;
import com.wedriveu.shared.rabbitmq.message.EnterVehicleResponse;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.mobile.util.Constants.NO_RESPONSE_DATA_ERROR;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class EnterVehicleServiceImpl implements EnterVehicleService {

    private static final String TAG = EnterVehicleServiceImpl.class.getSimpleName();
    private static final String ENTER_CONFIRMATION = "confirmed";
    private static final String ENTER_ERROR = "Error occurred while confirming enter operation.";

    private Activity mActivity;
    private UserStore mUserStore;
    private BookingStore mBookingStore;
    private RabbitMqCommunicationManager mCommunicationManager;

    public EnterVehicleServiceImpl(Activity activity, UserStore userStore, BookingStore bookingStore) {
        mActivity = activity;
        mUserStore = userStore;
        mBookingStore = bookingStore;
        mCommunicationManager = new DefaultRabbitMqCommunicationManager();
    }

    @Override
    public void subscribeToEnterVehicle(final ServiceOperationCallback<EnterVehicleRequest> callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                ServiceResult<EnterVehicleRequest> result;
                try {
                    ExceptionHandler exceptionHandler = new ServiceExceptionHandler();
                    RabbitMqCommunicationConfig config =
                            new RabbitMqCommunicationConfig.Builder()
                                    .host(Constants.RabbitMQ.Broker.HOST)
                                    .password(Constants.RabbitMQ.Broker.PASSWORD)
                                    .exceptionHandler(exceptionHandler).build();
                    mCommunicationManager.setUpCommunication(config);
                    final BlockingQueue<EnterVehicleRequest> response = new ArrayBlockingQueue<>(1);
                    EnterVehicleRequest request = subscribeForRequest(response);
                    result = createServiceResult(request);
                    EnterVehicleResponse enterVehicleResponse = new EnterVehicleResponse();
                    enterVehicleResponse.setResponse(ENTER_CONFIRMATION);
                    if (mBookingStore.getBooking().getLicensePlate().equals(request.getLicensePlate())) {
                        sendResponse(enterVehicleResponse);
                    } else {
                        result = new ServiceResult<>(null, ENTER_ERROR);
                    }
                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e(TAG, ENTER_ERROR, e);
                    result = new ServiceResult<>(null, ENTER_ERROR);
                }
                handleResponse(result, callback);
                return null;
            }
        }.execute();
    }

    private EnterVehicleRequest subscribeForRequest(final BlockingQueue<EnterVehicleRequest> responseBlockingQueue)
            throws IOException, InterruptedException {
        RabbitMqConsumerStrategy<EnterVehicleRequest> strategy =
                new EnterVehicleConsumerStrategy(mUserStore.getUser(), responseBlockingQueue);
        mCommunicationManager.registerConsumer(strategy, EnterVehicleRequest.class);
        return responseBlockingQueue.poll(com.wedriveu.mobile.util.Constants.SERVICE_OPERATION_TIMEOUT,
                TimeUnit.MILLISECONDS);

    }

    private ServiceResult<EnterVehicleRequest> createServiceResult(EnterVehicleRequest response)
            throws IOException {
        String error = "";
        if (response == null) {
            error = NO_RESPONSE_DATA_ERROR;
        }
        return new ServiceResult<>(response, error);
    }


    private void sendResponse(EnterVehicleResponse response) throws IOException {
        mCommunicationManager.publishMessage(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE_ENTER_USER,
                response);
    }

    private void handleResponse(final ServiceResult<EnterVehicleRequest> result,
                                final ServiceOperationCallback<EnterVehicleRequest> callback) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onServiceOperationFinished(result);
            }
        });
    }

}
