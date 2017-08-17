package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.rabbitmq.message.RegisterToServiceResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.REGISTER_RESULT;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.BOOKING_SERVICE_BOOK_RESPONSE;
import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.REGISTER_RESPONSE;
import static com.wedriveu.shared.util.Constants.Vehicle.LICENSE_PLATE;


/**
 * Vert.x RabbitMQ Publisher for vehicle register response. Replies the vehicle with the adding result to the database.
 *
 * @author Marco Baldassarri on 08/08/2017.
 */
public class BookPublisherVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.BookingControl.PUBLISH_RESULT, this::notifyBookingService);
        super.start(startFuture);
    }

    private void notifyBookingService(Message message) {
        BookVehicleResponse vehicleResponse = (BookVehicleResponse) message.body();
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                BOOKING_SERVICE_BOOK_RESPONSE,
                VertxJsonMapper.mapInBodyFrom(vehicleResponse), onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(BookPublisherVerticle.class.getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }
}
