package com.wedriveu.services.vehicle.nearest.boundary.finder;

import com.wedriveu.services.shared.rabbitmq.BasicConsumer;
import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.vehicle.app.Messages;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Marco Baldassarri
 * @since 30/07/2017
 */
public class FinderConsumer extends BasicConsumer {


    public FinderConsumer() {
        super(Constants.CONSUMER_VEHICLE_SERVICE);
    }

    public void startFinderConsumer() throws IOException, TimeoutException {
        BasicConsumer consumer = new FinderConsumer();
        consumer.start(onStart -> {
            consumer.declareQueue(onQueue -> {
                if (onQueue.succeeded()) {
                    consumer.bindQueueToExchange(Constants.VEHICLE_SERVICE_EXCHANGE,
                            Constants.ROUTING_KEY_CAN_DRIVE_RESPONSE, onBind -> {
                                if (onBind.succeeded()) {
                                    consumer.registerConsumer(Constants.EVENT_BUS_FINDER_ADDRESS);
                                    consumer.basicConsume(Constants.EVENT_BUS_FINDER_ADDRESS);
                                }
                            });
                }
            });
        });
    }


    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            vertx.eventBus().send(Messages.FinderConsumer.VEHICLE_RESPONSE, msg.body());
        });
    }

}
