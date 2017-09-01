package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.rabbitmq.client.RabbitMQClientFactory;
import com.wedriveu.shared.util.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.shared.util.Constants.RabbitMQ.Exchanges.Type.DIRECT;

/**
 * This Verticle is aim to declare and bind the exchange chosen by the Service at its startup.
 *
 * @author Marco Baldassarri
 * @author Stefano Bernagozzi
 * @since 11/08/2017.
 */
public class ExchangeManagerVerticleWithQueue extends AbstractVerticle {

    private String eventBusReceiver;
    private String eventBusSend;
    private RabbitMQClient client;
    private String TAG = ExchangeManagerVerticleWithQueue.class.getSimpleName();

    public ExchangeManagerVerticleWithQueue(String eventBusReceiver, String eventBusSend) {
        this.eventBusReceiver = eventBusReceiver;
        this.eventBusSend = eventBusSend;
    }

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(eventBusReceiver, this::bindExchange);
    }

    private void bindExchange(Message message) {
        String exchangeName = (String) message.body();
        client = RabbitMQClientFactory.createClient(vertx);
        client.start(started -> {
            declareExchange(exchangeName);
        });
    }

    private void declareExchange(String exchangeName) {
        client.exchangeDeclare(exchangeName, DIRECT, true, false, onDeclareCompleted -> {
            if (onDeclareCompleted.succeeded()) {
                vertx.eventBus().send(eventBusSend, null);
            } else {
                Log.error(TAG, onDeclareCompleted.cause().getMessage(), onDeclareCompleted.cause());
            }
        });
    }

}
