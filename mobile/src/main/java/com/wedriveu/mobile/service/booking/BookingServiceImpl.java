package com.wedriveu.mobile.service.booking;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.rabbitmq.client.ExceptionHandler;
import com.wedriveu.mobile.model.Booking;
import com.wedriveu.mobile.service.ServiceExceptionHandler;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.shared.rabbitmq.communication.DefaultRabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunicationManager;
import com.wedriveu.shared.rabbitmq.communication.config.RabbitMqCommunicationConfig;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqCloseCommunicationStrategy;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;
import com.wedriveu.shared.rabbitmq.message.CreateBookingRequest;
import com.wedriveu.shared.rabbitmq.message.CreateBookingResponse;
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
 * @author Nicola Lasagni on 19/08/2017.
 */
public class BookingServiceImpl implements BookingService {

    private static final String TAG = BookingServiceImpl.class.getSimpleName();
    private static final String BOOKING_ERROR = "Error occurred while performing scheduling operation.";

    private Activity mActivity;
    private UserStore mUserStore;
    private RabbitMqCommunicationManager mCommunicationManager;

    public BookingServiceImpl(Activity activity, UserStore userStore) {
        mActivity = activity;
        mUserStore = userStore;
        mCommunicationManager = new DefaultRabbitMqCommunicationManager();
    }

    @Override
    public void acceptBooking(final Booking booking, final ServiceOperationCallback<CreateBookingResponse> callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ServiceResult<CreateBookingResponse> result;
                try {
                    ExceptionHandler exceptionHandler = new ServiceExceptionHandler();
                    RabbitMqCommunicationConfig config =
                            new RabbitMqCommunicationConfig.Builder()
                                    .host(Constants.RabbitMQ.Broker.HOST)
                                    .password(Constants.RabbitMQ.Broker.PASSWORD)
                                    .exceptionHandler(exceptionHandler).build();
                    mCommunicationManager.setUpCommunication(config);
                    CreateBookingRequest request = createRequest(booking);
                    sendRequest(request);
                    final BlockingQueue<CreateBookingResponse> response = new ArrayBlockingQueue<>(1);
                    CreateBookingResponse responseBody = subscribeForResponse(response);
                    result = createServiceResult(responseBody);
                    closeCommunication();
                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e(TAG, BOOKING_ERROR, e);
                    result = new ServiceResult<>(null, BOOKING_ERROR);
                }
                handleResponse(result, callback);
                return null;
            }
        }.execute();
    }

    private void closeCommunication() {
        try {
            RabbitMqCloseCommunicationStrategy strategy =
                    new BookingCloseCommunicationStrategy(mUserStore.getUser());
            mCommunicationManager.closeCommunication(strategy);
        } catch (IOException | TimeoutException e) {
            Log.e(TAG, CLOSE_COMMUNICATION_ERROR, e);
        }
    }

    private CreateBookingRequest createRequest(Booking booking) throws UnsupportedEncodingException {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setUsername(mUserStore.getUser().getUsername());
        request.setLicensePlate(booking.getLicensePlate());
        request.setUserPosition(booking.getUserPosition());
        request.setDestinationPosition(booking.getDestinationPosition());
        return request;
    }

    private void sendRequest(CreateBookingRequest request) throws IOException {
        mCommunicationManager.publishMessage(Constants.RabbitMQ.Exchanges.BOOKING,
                Constants.RabbitMQ.RoutingKey.CREATE_BOOKING_REQUEST,
                request);
    }

    private CreateBookingResponse subscribeForResponse(final BlockingQueue<CreateBookingResponse> responseBlockingQueue)
            throws IOException, InterruptedException {
        RabbitMqConsumerStrategy<CreateBookingResponse> strategy =
                new BookingConsumerStrategy(mUserStore.getUser(), responseBlockingQueue);
        mCommunicationManager.registerConsumer(strategy, CreateBookingResponse.class);
        return responseBlockingQueue.poll(com.wedriveu.mobile.util.Constants.SERVICE_OPERATION_TIMEOUT,
                TimeUnit.MILLISECONDS);

    }

    private ServiceResult<CreateBookingResponse> createServiceResult(CreateBookingResponse response)
            throws IOException {
        String error;
        if (response == null) {
            error = NO_RESPONSE_DATA_ERROR;
        } else {
            error = response.getErrorMessage();
        }
        return new ServiceResult<>(response, error);
    }

    private void handleResponse(final ServiceResult<CreateBookingResponse> result,
                                final ServiceOperationCallback<CreateBookingResponse> callback) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onServiceOperationFinished(result);
            }
        });
    }

}
