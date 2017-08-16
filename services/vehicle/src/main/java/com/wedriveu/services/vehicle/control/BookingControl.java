package com.wedriveu.services.vehicle.control;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.services.shared.entity.Vehicle;
import com.wedriveu.services.shared.rabbitmq.nearest.VehicleResponseCanDrive;
import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.boundary.nearest.VehicleFinderVerticle;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.wedriveu.services.shared.entity.Vehicle.NO_ELIGIBLE_VEHICLE_RESPONSE;
import static com.wedriveu.shared.util.Constants.*;
import static com.wedriveu.shared.util.Constants.Vehicle.LICENSE_PLATE;


/**
 * This Controller serves all the interaction requests with the
 * {@linkplain com.wedriveu.services.vehicle.entity.VehicleStore} database. It handles the database replies
 * by deploying and interacting with other Verticles.
 *
 * @author Marco Baldassarri on 02/08/2017.
 */
public class BookingControl extends AbstractVerticle {

    private EventBus eventBus;

    @Override
    public void start() throws Exception {
        this.eventBus = vertx.eventBus();
        eventBus.consumer(Messages.Booking.BOOK_RESPONSE, this::getDriveTimes);
        eventBus.consumer(Messages.Booking.UNDEPLOY, this::undeployBookVehicleVerticle);
    }

    private void getDriveTimes(Message message) {
        BookVehicleResponse vehicleResponse = (BookVehicleResponse) message.body();
        //if booked then send the vehicleResponse to the BookPublisherVerticle as it is
        //if not booked, calculate drive times, add them to the vehicleResponse
        // and then send the vehicleResponse to the BookPublisherVerticle
    }

    private void undeployBookVehicleVerticle(Message message) {
        String deploymentId = (String) message.body();
        if (deploymentId != null && !deploymentId.isEmpty()) {
            vertx.undeploy(deploymentId);
        }
    }

}


