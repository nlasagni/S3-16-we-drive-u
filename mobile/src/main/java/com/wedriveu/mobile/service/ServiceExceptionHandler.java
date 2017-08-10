package com.wedriveu.mobile.service;

import android.app.Activity;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

/**
 * The default {@linkplain ServiceExceptionHandler} used to manage message consumer exceptions.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
public class ServiceExceptionHandler extends DefaultExceptionHandler {

    private Activity mActivity;
    private ServiceOperationCallback<?> mCallback;

    public ServiceExceptionHandler(Activity activity, ServiceOperationCallback<?> callback) {
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
                mCallback.onServiceOperationFinished(new ServiceResult(null, exception.getLocalizedMessage()));
            }
        });
    }

}
