package com.wedriveu.mobile.service.scheduling;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.rabbitmq.client.*;
import com.wedriveu.mobile.model.SchedulingLocation;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.scheduling.model.VehicleResponse;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.mobile.util.RabbitMQJsonMapper;
import com.wedriveu.shared.entity.Position;
import com.wedriveu.shared.entity.VehicleRequest;
import com.wedriveu.shared.util.Constants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.mobile.util.Constants.CLOSE_COMMUNICATION_ERROR;
import static com.wedriveu.mobile.util.Constants.NO_RESPONSE_DATA_ERROR;
import static com.wedriveu.mobile.util.Constants.TIME_OUT_ERROR;

/**
 * @author Marco Baldassarri on 18/07/2017.
 * @author Nicola Lasagni on 09/08/2017.
 */
public class SchedulingServiceImpl implements SchedulingService {

    private static final String SCHEDULING_ERROR = "Error occurred while performing scheduling operation.";

    private Activity mActivity;
    private UserStore mUserStore;
    private Double mUserLatitude;
    private Double mUserLongitude;
    private SchedulingLocation schedulingLocation;

    public SchedulingServiceImpl(Activity activity, UserStore userStore) {
        mActivity = activity;
        mUserStore = userStore;
        schedulingLocation = new SchedulingLocation();
    }

    @Override
    public void findNearestVehicle(final Place address, final SchedulingServiceCallback callback) {
        schedulingLocation.setDestinationLatitude(address.getLatLng().latitude);
        schedulingLocation.setDestinationLongitude(address.getLatLng().longitude);


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ExceptionHandler exceptionHandler = new SchedulingServiceExceptionHandler(mActivity, callback);
                    ConnectionFactory connectionFactory =
                            createConnectionFactory(exceptionHandler,
                                    Constants.RabbitMQ.Broker.HOST,
                                    Constants.RabbitMQ.Broker.PASSWORD);
                    Connection connection = createConnection(connectionFactory);
                    Channel channel = createChannel(connection);
                    if (channel != null) {
                        VehicleRequest request = createRequest(address);
                        findNearest(channel, request);
                        subscribeForResponse(connection, channel, request, callback);
                    }
                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e(TAG, SCHEDULING_ERROR, e);
                    handleResponse(null, SCHEDULING_ERROR, callback);
                }
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

    private ConnectionFactory createConnectionFactory(ExceptionHandler exceptionHandler,
                                                      String host,
                                                      String password) {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setExceptionHandler(exceptionHandler);
        connectionFactory.setHost(host);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    private Connection createConnection(ConnectionFactory connectionFactory) throws IOException, TimeoutException {
        return connectionFactory.newConnection();
    }

    private Channel createChannel(Connection connection) throws IOException {
        return connection.createChannel();
    }

    private void closeCommunication(Connection connection, Channel channel, String queue) {
        try {
            if (channel != null) {
                String userName = mUserStore.getUser().getUsername();
                String routingKey = String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE, userName);
                channel.queueUnbind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
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

    private void findNearest(Channel channel, VehicleRequest request) throws IOException {
        byte[] body = RabbitMQJsonMapper.mapToByteArray(request);
        channel.basicPublish(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_REQUEST,
                null,
                body);
    }

    private void subscribeForResponse(final Connection connection,
                                      final Channel channel,
                                      final VehicleRequest request,
                                      final SchedulingServiceCallback callback) throws IOException, InterruptedException {
        final BlockingQueue<byte[]> response = new ArrayBlockingQueue<>(1);
        String userName = mUserStore.getUser().getUsername();
        String queue = String.format(Constants.RabbitMQ.Queue.USER, userName);
        String routingKey = String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_RESPONSE, userName);
        String userQueue = String.format(Constants.RabbitMQ.Queue.USER, request.getUsername());
        channel.queueDeclare(userQueue, true, false, true, null);
        channel.queueBind(queue, Constants.RabbitMQ.Exchanges.VEHICLE, routingKey);
        channel.basicConsume(queue, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                response.offer(body);
            }

            @Override
            public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                handleResponse(null, sig.getLocalizedMessage(), callback);
            }
        });
        byte[] responseBody =
                response.poll(com.wedriveu.mobile.util.Constants.SERVICE_OPERATION_TIMEOUT, TimeUnit.MILLISECONDS);
        closeCommunication(connection, channel, queue);
        handleResponseDelivery(responseBody, callback);
    }

    private void handleResponseDelivery(byte[] body,
                                        final SchedulingServiceCallback callback) throws IOException {
        Vehicle vehicle = null;
        String error = "";
        if (body == null) {
            error = TIME_OUT_ERROR;
        } else if (body.length == 0) {
            error = NO_RESPONSE_DATA_ERROR;
        } else {
            VehicleResponse response = RabbitMQJsonMapper.mapFromByteArray(body, VehicleResponse.class);
            if (response != null && !TextUtils.isEmpty(response.getLicencePlate())) {
                vehicle = new Vehicle(response.getLicencePlate(),
                        response.getVehicleName(),
                        response.getDescription(),
                        response.getPictureURL(),
                        response.getArriveAtUserTime(),
                        response.getArriveAtDestinationTime());
            } else {
                error = NO_RESPONSE_DATA_ERROR;
            }
        }
        handleResponse(vehicle, error, callback);
    }

    private void handleResponse(final Vehicle vehicle,
                                final String error,
                                final SchedulingServiceCallback callback) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFindNearestVehicleFinished(vehicle, error);
            }
        });
    }

}
