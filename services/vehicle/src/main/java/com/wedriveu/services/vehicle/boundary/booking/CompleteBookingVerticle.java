package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticleConsumer;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.ArrivedNotify;
import com.wedriveu.shared.rabbitmq.message.CompleteBookingRequest;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.services.vehicle.rabbitmq.Constants.VEHICLE_SERVICE_QUEUE_VEHICLE_ARRIVED;

/**
 * Boundary {@linkplain VerticleConsumer} that receives
 * {@linkplain Constants.RabbitMQ.RoutingKey#VEHICLE_ARRIVED} messages and publish
 * {@linkplain Constants.RabbitMQ.RoutingKey#COMPLETE_BOOKING_REQUEST} immediately to
 * the Booking Service.
 *
 * @author Michele on 19/08/2017.
 * @author Nicola Lasagni
 */
public class CompleteBookingVerticle extends VerticleConsumer {

    private static final String TAG = CompleteBookingVerticle.class.getSimpleName();
    private static final String EVENT_BUS_ADDRESS = "service.vehicle.arrived.eventbus";

    private EventBus eventBus;

    public CompleteBookingVerticle() {
        super(VEHICLE_SERVICE_QUEUE_VEHICLE_ARRIVED);
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start();
        eventBus = vertx.eventBus();
        startConsumerWithFuture(Constants.RabbitMQ.Exchanges.VEHICLE,
                Constants.RabbitMQ.RoutingKey.VEHICLE_ARRIVED,
                EVENT_BUS_ADDRESS,
                startFuture);
    }

    @Override
    public void registerConsumer(String eventBusAddress) {
        eventBus.consumer(eventBusAddress, msg -> {
            ArrivedNotify notify = VertxJsonMapper.mapFromBodyTo((JsonObject) msg.body(), ArrivedNotify.class);
            publishCompleteBooking(notify.getUsername(), notify.getLicense());
        });
    }

    private void publishCompleteBooking(String username, String licensePlate) {
        client.basicPublish(Constants.RabbitMQ.Exchanges.BOOKING,
                Constants.RabbitMQ.RoutingKey.COMPLETE_BOOKING_REQUEST,
                createObject(username, licensePlate),
                onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(TAG, onPublish.cause());
                    }
                });
    }

    private JsonObject createObject(String username, String licensePlate) {
        CompleteBookingRequest request = new CompleteBookingRequest();
        request.setUsername(username);
        request.setLicensePlate(licensePlate);
        return VertxJsonMapper.mapInBodyFrom(request);
    }

}
