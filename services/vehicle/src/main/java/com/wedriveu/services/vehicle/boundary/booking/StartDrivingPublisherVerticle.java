package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.shared.util.Constants.RabbitMQ.RoutingKey.START_DRIVING;


/**
 * Vert.x RabbitMQ Publisher Vehicle start driving request.
 * Once the VehicleService receives the Vehicle response, if it isn't already booked, it sends back to the Vehicle
 * the start driving request, sending UserPosition and DestinationPosition.
 *
 * @author Marco Baldassarri on 17/08/2017.
 */
public class StartDrivingPublisherVerticle extends VerticlePublisher {

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(Messages.BookingControl.START_DRIVING, this::sendStartDrivingCommand);
        super.start(startFuture);
    }

    private void sendStartDrivingCommand(Message message) {
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                START_DRIVING,
                ((JsonObject) message.body()), onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(StartDrivingPublisherVerticle.class.getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }

}
