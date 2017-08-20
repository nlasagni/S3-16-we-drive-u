package com.wedriveu.mobile.service;

import android.util.Log;
import com.rabbitmq.client.ShutdownSignalException;
import com.wedriveu.shared.rabbitmq.communication.RabbitMqCommunication;
import com.wedriveu.shared.rabbitmq.communication.strategy.RabbitMqConsumerStrategy;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * The {@link ServiceConsumerStrategy} abstract class that simply offers the message
 * consumed through the dedicated {@linkplain BlockingQueue<T>} and let the subclasses
 * define the {@linkplain RabbitMqConsumerStrategy#configureQueue(RabbitMqCommunication)}
 * implementation.
 *
 * @param <T> the type of the message consumed
 * @author Nicola Lasagni on 10/08/2017.
 */
public abstract class ServiceConsumerStrategy<T> implements RabbitMqConsumerStrategy<T> {

    private String mTag;
    private BlockingQueue<T> mResponse;

    public ServiceConsumerStrategy(String tag, BlockingQueue<T> response) {
        mTag = tag;
        mResponse = response;
    }

    @Override
    public abstract String configureQueue(RabbitMqCommunication communication) throws IOException;

    @Override
    public void handleMessage(T message) {
        //TODO
        com.wedriveu.shared.util.Log.info(this.getClass().getSimpleName(), "Received message");
        mResponse.offer(message);
    }

    @Override
    public void handleShutdown(ShutdownSignalException e) {
        Log.e(mTag, e.getLocalizedMessage(), e);
        mResponse.offer(null);
    }

}
