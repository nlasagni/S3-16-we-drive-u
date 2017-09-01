package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.AbortBookingRequest;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Boundary {@linkplain VerticleConsumer} that published
 * {@linkplain Constants.RabbitMQ.RoutingKey#ABORT_BOOKING_REQUEST} to the Booking Service.
 *
 * @author Nicola Lasagni on 30/08/2017.
 */
public class AbortBookingPublisherVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.Booking.ABORT_BOOKING, this::notifyBookingService);
        super.start(startFuture);
    }

    private void notifyBookingService(Message message) {
        AbortBookingRequest request =
                VertxJsonMapper.mapTo((JsonObject) message.body(), AbortBookingRequest.class);
        publish(Constants.RabbitMQ.Exchanges.BOOKING,
                Constants.RabbitMQ.RoutingKey.ABORT_BOOKING_REQUEST,
                VertxJsonMapper.mapInBodyFrom(request), onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(this.getClass().getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }

}
