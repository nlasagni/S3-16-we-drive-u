package com.wedriveu.mobile.service.booking;

import android.os.AsyncTask;
import android.os.Message;
import android.util.Log;
import com.rabbitmq.client.ExceptionHandler;
import com.wedriveu.mobile.model.Booking;
import com.wedriveu.mobile.service.ServiceExceptionHandler;
import com.wedriveu.mobile.service.ServiceOperationHandler;
import com.wedriveu.mobile.service.ServiceResult;
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
 * The effective {@linkplain BookingService} implementation
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public class BookingServiceImpl implements BookingService {

    private static final String TAG = BookingServiceImpl.class.getSimpleName();
    private static final String BOOKING_ERROR = "Error occurred while performing scheduling operation.";

    private RabbitMqCommunicationManager mCommunicationManager;

    /**
     * Instantiates a new BookingService.
     */
    public BookingServiceImpl() {
        mCommunicationManager = new DefaultRabbitMqCommunicationManager();
    }

    @Override
    public <T> void acceptBooking(final String username,
                                  final Booking booking,
                                  final ServiceOperationHandler<T, CreateBookingResponse> handler) {
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
                    CreateBookingRequest request = createRequest(username, booking);
                    sendRequest(request);
                    final BlockingQueue<CreateBookingResponse> response = new ArrayBlockingQueue<>(1);
                    CreateBookingResponse responseBody = subscribeForResponse(username, response);
                    result = createServiceResult(responseBody);
                    closeCommunication(username);
                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e(TAG, BOOKING_ERROR, e);
                    result = new ServiceResult<>(null, BOOKING_ERROR);
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
            RabbitMqCloseCommunicationStrategy strategy =
                    new BookingCloseCommunicationStrategy(username);
            mCommunicationManager.closeCommunication(strategy);
        } catch (IOException | TimeoutException e) {
            Log.e(TAG, CLOSE_COMMUNICATION_ERROR, e);
        }
    }

    private CreateBookingRequest createRequest(String username,
                                               Booking booking) throws UnsupportedEncodingException {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setUsername(username);
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

    private CreateBookingResponse subscribeForResponse(String username,
                                                       final BlockingQueue<CreateBookingResponse> responseBlockingQueue)
            throws IOException, InterruptedException {
        RabbitMqConsumerStrategy<CreateBookingResponse> strategy =
                new BookingSynchronousConsumerStrategy(username, responseBlockingQueue);
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

}
