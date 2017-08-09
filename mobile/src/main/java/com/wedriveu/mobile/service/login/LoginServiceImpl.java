package com.wedriveu.mobile.service.login;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.rabbitmq.client.*;
import com.wedriveu.mobile.model.User;
import com.wedriveu.shared.entity.LoginRequest;
import com.wedriveu.shared.entity.LoginResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.mobile.util.RabbitMQJsonMapper;

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
 * @author Marco Baldassarri
 * @author Nicola Lasagni
 * @since 18/07/2017
 *
 */
public class LoginServiceImpl implements LoginService {

    private static final String TAG = LoginServiceImpl.class.getSimpleName();
    private static final String LOGIN_ERROR = "Error occurred while performing login operation.";

    private Activity mActivity;

    public LoginServiceImpl(Activity activity) {
        mActivity = activity;
    }
    @Override
    public void login(final String username,
                      final String password,
                      final LoginServiceCallback callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    ExceptionHandler exceptionHandler = new LoginExceptionHandler(mActivity, callback);
                    ConnectionFactory connectionFactory =
                            createConnectionFactory(exceptionHandler,
                                    Constants.RabbitMQ.Broker.HOST,
                                    Constants.RabbitMQ.Broker.PASSWORD);
                    Connection connection = createConnection(connectionFactory);
                    Channel channel = createChannel(connection);
                    if (channel != null) {
                        String requestId = channel.queueDeclare().getQueue();
                        LoginRequest request = createRequest(requestId, username, password);
                        login(channel, request);
                        subscribeForResponse(connection, channel, request, callback);
                    }
                } catch (IOException | TimeoutException | InterruptedException e) {
                    Log.e(TAG, LOGIN_ERROR, e);
                    handleResponse(null, LOGIN_ERROR, callback);
                }
                return null;
            }


        }.execute();
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
                channel.queueDelete(queue);
                channel.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (IOException | TimeoutException e) {
            Log.e(TAG, CLOSE_COMMUNICATION_ERROR, e);
        }
    }

    private LoginRequest createRequest(String requestId,
                                       String username,
                                       String password) throws UnsupportedEncodingException {
        LoginRequest request = new LoginRequest();
        request.setRequestId(requestId);
        request.setUsername(username);
        request.setPassword(password);
        return request;
    }

    private void login(Channel channel, LoginRequest request) throws IOException {
        byte[] body = RabbitMQJsonMapper.mapToByteArray(request);
        channel.basicPublish(Constants.RabbitMQ.Exchanges.USER,
                Constants.RabbitMQ.RoutingKey.LOGIN,
                null,
                body);
    }

    private void subscribeForResponse(final Connection connection,
                                      final Channel channel,
                                      final LoginRequest request,
                                      final LoginServiceCallback callback) throws IOException, InterruptedException {
        final BlockingQueue<byte[]> response = new ArrayBlockingQueue<>(1);
        channel.basicConsume(request.getRequestId(), new DefaultConsumer(channel) {
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
        handleResponseDelivery(connection, channel, responseBody, request, callback);
    }

    private void handleResponseDelivery(Connection connection,
                                        Channel channel,
                                        byte[] body,
                                        final LoginRequest request,
                                        final LoginServiceCallback callback) throws IOException {
        User user = null;
        String error = "";
        if (body == null) {
            error = TIME_OUT_ERROR;
        } else if (body.length == 0) {
            error = NO_RESPONSE_DATA_ERROR;
        } else {
            LoginResponse response = RabbitMQJsonMapper.mapFromByteArray(body, LoginResponse.class);
            if (response.getSuccess()) {
                String userQueue = String.format(Constants.RabbitMQ.Queue.USER, request.getUsername());
                channel.queueDeclare(userQueue, true, false, true, null);
                closeCommunication(connection, channel, request.getRequestId());
                user = new User(request.getUsername(), request.getPassword());
            } else {
                error = response.getErrorMessage();
            }
        }
        handleResponse(user, error, callback);
    }

    private void handleResponse(final User user,
                                final String error,
                                final LoginServiceCallback callback) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onLoginFinished(user, error);
            }
        });
    }

}
