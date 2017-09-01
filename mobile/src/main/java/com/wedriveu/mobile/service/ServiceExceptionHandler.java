package com.wedriveu.mobile.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import com.wedriveu.shared.util.Log;

/**
 * The default {@linkplain ServiceExceptionHandler} used to manage consumer exceptions.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
public class ServiceExceptionHandler extends DefaultExceptionHandler {

    private static final String TAG = ServiceExceptionHandler.class.getSimpleName();
    private static final String EXCEPTION_MESSAGE = "Exception occurred on RabbitMQ consumer";

    @Override
    public void handleConsumerException(Channel channel,
                                        final Throwable exception,
                                        Consumer consumer,
                                        String consumerTag,
                                        String methodName) {
        Log.error(TAG, EXCEPTION_MESSAGE, exception);
    }

}
