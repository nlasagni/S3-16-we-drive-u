package com.wedriveu.mobile.service.login;

import android.app.Activity;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

/**
 * @author Nicola Lasagni on 08/08/2017.
 */
class LoginExceptionHandler extends DefaultExceptionHandler {

    private Activity mActivity;
    private LoginServiceCallback mCallback;

    LoginExceptionHandler(Activity activity, LoginServiceCallback callback) {
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
                mCallback.onLoginFinished(null, exception.getLocalizedMessage());
            }
        });
    }
}
