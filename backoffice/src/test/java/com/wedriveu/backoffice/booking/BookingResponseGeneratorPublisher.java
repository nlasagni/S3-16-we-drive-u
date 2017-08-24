package com.wedriveu.backoffice.booking;

import com.wedriveu.backoffice.analytics.VehicleCounterGenerator;
import com.wedriveu.backoffice.util.ConstantsBackoffice;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ROUTING_KEY_ANALYTICS_RESPONSE_VEHICLE_LIST;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.ROUTING_KEY_BOOKING_RESPONSE_BOOKING_LIST;

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
        vertx.eventBus().consumer(ConstantsBackoffice.EventBus.BACKOFFICE_BOOKING_LIST_RESPONSE_GENERATOR_START_TEST, this::sendVehicleCounterToBackOffice);
    }

    private void sendVehicleCounterToBackOffice(Message message) {
        try {
            JsonObject dataToUser = VertxJsonMapper.mapListInBodyFrom(BookingListGenerator.getBookingsFromBookingService());
            publish(Constants.RabbitMQ.Exchanges.BOOKING,
                    String.format(Constants.RabbitMQ.RoutingKey.ROUTING_KEY_BOOKING_RESPONSE_BOOKING_LIST, ConstantsBackoffice.TEST_BACKOFFICE_ID),
                    dataToUser,
                    res -> {
                   });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
