package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.FindBookingPositionsRequest;
import com.wedriveu.shared.rabbitmq.message.FindBookingPositionsResponse;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.EVENT_BUS_FIND_BOOKING_POSITION_ADDRESS;
import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_FIND_BOOKING_POSITIONS;


/**
 * Boundary {@linkplain VerticleConsumer} that receives
 * {@linkplain Constants.RabbitMQ.RoutingKey#FIND_BOOKING_POSITION_RESPONSE} messages and
 * redirect them to the proper control.
 * Also, it is capable to publish {@linkplain Constants.RabbitMQ.RoutingKey#FIND_BOOKING_POSITION_REQUEST} to
 * the Booking Service.
 *
 * @author Nicola Lasagni
 */
public class GetBookingPositionsVerticle extends VerticleConsumer {

    public GetBookingPositionsVerticle() {
        super(VEHICLE_SERVICE_QUEUE_FIND_BOOKING_POSITIONS);
    }

    @Override
    public void start(Future startFuture) throws Exception {
        super.start();
        vertx.eventBus().consumer(Messages.Booking.GET_BOOKING_POSITIONS, this::notifyBookingService);
        startConsumer(startFuture);
    }

    private void startConsumer(Future future) throws IOException, TimeoutException {
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.BOOKING,
                Constants.RabbitMQ.RoutingKey.FIND_BOOKING_POSITION_RESPONSE,
                EVENT_BUS_FIND_BOOKING_POSITION_ADDRESS,
                future);
    }

    @Override
    public void registerConsumer(String eventBus) {
        vertx.eventBus().consumer(eventBus, msg -> {
            sendFindBookingPositionsResponse((JsonObject) msg.body());
        });
    }

    private void sendFindBookingPositionsResponse(JsonObject requestMessage) {
        FindBookingPositionsResponse response =
                VertxJsonMapper.mapFromBodyTo(requestMessage, FindBookingPositionsResponse.class);
        vertx.eventBus().send(String.format(Messages.Booking.GET_BOOKING_POSITIONS_COMPLETED,
                response.getLicensePlate()),
                requestMessage);
    }

    private void notifyBookingService(Message message) {
        FindBookingPositionsRequest request =
                VertxJsonMapper.mapTo((JsonObject) message.body(), FindBookingPositionsRequest.class);
        client.basicPublish(Constants.RabbitMQ.Exchanges.BOOKING,
                Constants.RabbitMQ.RoutingKey.FIND_BOOKING_POSITION_REQUEST,
                VertxJsonMapper.mapInBodyFrom(request), onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(this.getClass().getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }

}
