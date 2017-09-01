package com.wedriveu.backoffice.booking;

import com.wedriveu.backoffice.util.ConstantsBackOffice;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * @author Stefano Bernagozzi
 */
public class BookingResponseGeneratorPublisher extends VerticlePublisher{
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Future future = Future.future();
        super.start(future);
        future.setHandler(res -> {
            startFuture.complete();
            startConsumer();
        });
    }

    private void startConsumer() {
        vertx.eventBus().consumer(ConstantsBackOffice.EventBus.BACKOFFICE_BOOKING_LIST_RESPONSE_GENERATOR_START_TEST, this::sendVehicleCounterToBackOffice);
    }

    private void sendVehicleCounterToBackOffice(Message message) {
        try {
            JsonObject dataToUser = VertxJsonMapper.mapListInBodyFrom(BookingListGenerator.getBookingsFromBookingService());
            publish(Constants.RabbitMQ.Exchanges.BOOKING,
                    String.format(Constants.RabbitMQ.RoutingKey.BOOKING_RESPONSE_BOOKING_LIST, ConstantsBackOffice.TEST_BACKOFFICE_ID),
                    dataToUser,
                    res -> {
                   });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
