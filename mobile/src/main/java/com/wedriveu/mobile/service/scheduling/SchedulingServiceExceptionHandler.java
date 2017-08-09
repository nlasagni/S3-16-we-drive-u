package com.wedriveu.mobile.service.scheduling;

import android.app.Activity;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

/**
 * @author Nicola Lasagni on 09/08/2017.
 */
public class SchedulingServiceExceptionHandler extends DefaultExceptionHandler {

    private Activity mActivity;
    private SchedulingServiceCallback mCallback;

    SchedulingServiceExceptionHandler(Activity activity, SchedulingServiceCallback callback) {
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
                mCallback.onFindNearestVehicleFinished(null, exception.getLocalizedMessage());
            }
        });
    }

}
