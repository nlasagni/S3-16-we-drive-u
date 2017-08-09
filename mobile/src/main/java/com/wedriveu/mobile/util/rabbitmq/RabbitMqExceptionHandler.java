package com.wedriveu.mobile.util.rabbitmq;

import android.app.Activity;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import com.wedriveu.mobile.service.ServiceOperationCallback;

/**
 * @author Nicola Lasagni on 09/08/2017.
 */
public class RabbitMqExceptionHandler<T> extends DefaultExceptionHandler {

    private Activity mActivity;
    private ServiceOperationCallback<T> mCallback;

    public RabbitMqExceptionHandler(Activity activity, ServiceOperationCallback<T> callback) {
        mActivity = activity;
        mCallback = callback;
    }

    @Override
    public void handleConsumerException(Channel channel,
                                        final Throwable exception,
                                        Consumer consumer,
                                        String consumerTag,
                                        String methodName) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onServiceOperationFinished(null, exception.getLocalizedMessage());
            }
        });
    }

}
