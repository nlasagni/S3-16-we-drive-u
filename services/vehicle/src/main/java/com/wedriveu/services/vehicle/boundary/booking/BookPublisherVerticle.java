package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.VEHICLE_SERVICE_BOOK_RESPONSE;


/**
 * Vert.x RabbitMQ Publisher. Replies the BookingService with the booking results.
 *
 * @author Marco Baldassarri on 17/08/2017.
 */
public class BookPublisherVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.BookingControl.PUBLISH_RESULT, this::notifyBookingService);
        super.start(startFuture);
    }

    private void notifyBookingService(Message message) {
        BookVehicleResponse vehicleResponse =
                VertxJsonMapper.mapTo((JsonObject) message.body(), BookVehicleResponse.class);
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                VEHICLE_SERVICE_BOOK_RESPONSE,
                VertxJsonMapper.mapInBodyFrom(vehicleResponse), onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(BookPublisherVerticle.class.getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }
}
