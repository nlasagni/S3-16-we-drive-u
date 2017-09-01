package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.vehicle.rabbitmq.Messages;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

import java.util.UUID;


/**
 * This Controller serves some interaction requests with the
 * {@linkplain com.wedriveu.services.vehicle.entity.VehicleStore} database. It handles the database replies
 * by deploying and interacting with other Verticles.
 * BookingControl calculates the drive times to the user and to the destinations, undeploys short-life verticles
 * like {@linkplain com.wedriveu.services.vehicle.boundary.booking.BookVehicleVerticle} and send back data
 * to the publishers related to the BookingService and the booking process.
 *
 * @author Marco Baldassarri on 17/08/2017.
 * @author Nicola Lasagni
 */
public class BookingControl extends AbstractVerticle {

    private EventBus eventBus;

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.Booking.BOOK_RESPONSE, this::handleBookResponse);
        eventBus.consumer(Messages.Booking.UNDEPLOY, this::undeployVerticle);
    }

    private void handleBookResponse(Message message) {
        String id = UUID.randomUUID().toString();
        vertx.deployVerticle(new BookingSessionManager(id), handler ->
            eventBus.send(Messages.Booking.START_VEHICLE, message.body())
        );
    }
    private void undeployVerticle(Message message) {
        String deploymentId = (String) message.body();
        if (deploymentId != null && !deploymentId.isEmpty()) {
            vertx.undeploy(deploymentId);
        }
    }

}


