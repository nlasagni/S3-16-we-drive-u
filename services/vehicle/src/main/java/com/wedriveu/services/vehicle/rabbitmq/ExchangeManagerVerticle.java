package com.wedriveu.services.vehicle.rabbitmq;

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
 */
public class ExchangeManagerVerticle extends AbstractVerticle {

    private static RabbitMQClient client;
    private static String TAG = ExchangeManagerVerticle.class.getSimpleName();

    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer(Messages.VehicleService.DECLARE_EXCHANGE, this::bindExchange);
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
                vertx.eventBus().send(Messages.VehicleService.EXCHANGE_DECLARED, null);
            } else {
                Log.error(TAG, onDeclareCompleted.cause().getMessage(), onDeclareCompleted.cause());
            }
        });
    }

}
