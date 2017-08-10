package com.wedriveu.mobile.service.login;

import com.wedriveu.mobile.service.ServiceConsumerStrategy;
import com.wedriveu.shared.entity.LoginResponse;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * The type Login consumer strategy.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
class LoginConsumerStrategy  extends ServiceConsumerStrategy<LoginResponse> {

    private static final String TAG = LoginConsumerStrategy.class.getSimpleName();

    private String mRequestId;

    LoginConsumerStrategy(String requestId, BlockingQueue<LoginResponse> response) {
        super(TAG, response);
        mRequestId = requestId;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        return mRequestId;
    }

}
