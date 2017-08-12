package com.wedriveu.services.shared.rabbitmq;

import com.wedriveu.services.shared.rabbitmq.client.RabbitMQFactory;
import com.wedriveu.services.shared.utilities.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.rabbitmq.RabbitMQClient;

import static com.wedriveu.services.shared.utilities.Constants.EXCHANGE_TYPE;

/**
 * Created by Marco on 11/08/2017.
 */
public class ExchangeManagerVerticle extends AbstractVerticle {

    private static RabbitMQClient client;
    private static String TAG = ExchangeManagerVerticle.class.getSimpleName();

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(SharedMessages.VehicleService.BIND_EXCHANGE, this::bindExchange);
    }

    private void bindExchange(Message message) {
        String exchangeName = (String) message.body();
        client = RabbitMQFactory.createClient(vertx);
        client.start(started -> {
            declareExchange(exchangeName);
        });
    }

    private void declareExchange(String exchangeName) {
        client.exchangeDeclare(exchangeName, EXCHANGE_TYPE, false, false, onDeclareCompleted -> {
            if (onDeclareCompleted.succeeded()) {
                vertx.eventBus().send(SharedMessages.VehicleService.EXCHANGE_BINDED, null);
            } else {
                Log.error(TAG, onDeclareCompleted.cause().getMessage(), onDeclareCompleted.cause());
            }
        });
    }

}
