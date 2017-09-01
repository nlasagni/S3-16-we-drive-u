package com.wedriveu.backoffice.booking;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.BookingListRequest;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author Stefano Bernagozzi
 */
public class BookingRequestReceiverConsumer extends VerticleConsumer {

    public BookingRequestReceiverConsumer() {
        super(ConstantsBackOffice.Queues.ANALYTYCS_BOOKING_LIST_REQUEST_QUEUE_TEST);
    }

    @Override
    public void start(Future futureRetriever) throws Exception {
        super.start();
        Future<Void> futureConsumer = Future.future();
        futureConsumer.setHandler(v -> {
            if (v.succeeded()) {
                futureRetriever.complete();
            } else {
                futureRetriever.fail(v.cause());
            }
        });

        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.BOOKING,
                Constants.RabbitMQ.RoutingKey.BOOKING_REQUEST_BOOKING_LIST,
                ConstantsBackOffice.EventBus.ANALYTICS_BOOKING_LIST_REQUEST_EVENTBUS_AVAILABLE_TEST,
                futureConsumer);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, this::sendUpdatesToController);
    }

    private void sendUpdatesToController(Message message) {
        BookingListRequest bookingListRequest = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), BookingListRequest.class);
        vertx.eventBus().send(ConstantsBackOffice.EventBus.BACKOFFICE_BOOKING_LIST_REQUEST_TEST, VertxJsonMapper.mapInBodyFrom(bookingListRequest));
    }

}
