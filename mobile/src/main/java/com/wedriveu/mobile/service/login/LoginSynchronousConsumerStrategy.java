package com.wedriveu.mobile.service.login;

import com.wedriveu.mobile.service.ServiceSynchronousConsumerStrategy;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.message.LoginResponse;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * The {@linkplain ServiceSynchronousConsumerStrategy} to manage a {@linkplain LoginResponse}.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
class LoginSynchronousConsumerStrategy extends ServiceSynchronousConsumerStrategy<LoginResponse> {

    private String mRequestId;

    /**
     * Instantiates a new LoginSynchronousConsumerStrategy.
     *
     * @param requestId the request id
     * @param response  the {@linkplain LoginResponse} received
     */
    LoginSynchronousConsumerStrategy(String requestId, BlockingQueue<LoginResponse> response) {
        super(response);
        mRequestId = requestId;
    }

    @Override
    public String configureQueue(RabbitMqCommunication communication) throws IOException {
        return mRequestId;
    }

}
