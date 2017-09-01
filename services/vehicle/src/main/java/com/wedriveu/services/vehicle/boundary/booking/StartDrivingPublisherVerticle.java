package com.wedriveu.services.vehicle.boundary.booking;

import com.wedriveu.services.shared.rabbitmq.VerticlePublisher;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.shared.rabbitmq.message.DriveCommand;
import com.wedriveu.shared.util.Constants;
import com.wedriveu.shared.util.Log;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static com.wedriveu.services.vehicle.rabbitmq.Messages.BookingControl.START_DRIVING;


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
        vertx.eventBus().consumer(START_DRIVING, this::sendStartDrivingCommand);
        super.start(startFuture);
    }

    private void sendStartDrivingCommand(Message message) {
        DriveCommand command = VertxJsonMapper.mapFromBodyTo((JsonObject) message.body(), DriveCommand.class);
        publish(Constants.RabbitMQ.Exchanges.VEHICLE,
                String.format(Constants.RabbitMQ.RoutingKey.VEHICLE_DRIVE_COMMAND, command.getLicensePlate()),
                ((JsonObject) message.body()), onPublish -> {
                    if (!onPublish.succeeded()) {
                        Log.error(StartDrivingPublisherVerticle.class.getSimpleName(),
                                onPublish.cause().getMessage(), onPublish.cause());
                    }
                });
    }

}
