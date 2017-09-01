package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.shared.vertx.VertxJsonMapper;
import com.wedriveu.services.vehicle.rabbitmq.Messages;
import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;
import com.wedriveu.shared.util.Constants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * This {@linkplain AbstractVerticle} manages the {@linkplain VehicleUpdate}, if there is an update
 * with status {@linkplain Constants.Vehicle#STATUS_BROKEN_STOLEN} it delegates the substitution process
 * to the {@linkplain SubstitutionControl}.
 *
 * @author Nicola Lasagni on 25/08/2017.
 */
public class UpdateControl extends AbstractVerticle {

    private EventBus eventBus;

    @Override
    public void start() throws Exception {
        eventBus = vertx.eventBus();
        eventBus.consumer(Messages.UpdateControl.UPDATE_VEHICLE_STATUS, this::updateVehicleStatus);
    }

    private void updateVehicleStatus(Message message) {
        JsonObject body = (JsonObject) message.body();
        VehicleUpdate update = VertxJsonMapper.mapFromBodyTo(body, VehicleUpdate.class);
        if (Constants.Vehicle.STATUS_BROKEN_STOLEN.equals(update.getStatus())) {
            vertx.eventBus().send(Messages.VehicleSubstitution.START_SUBSTITUTION, message.body());
        } else {
            eventBus.send(Messages.VehicleStore.UPDATE_VEHICLE_STATUS, VertxJsonMapper.mapInBodyFrom(update));
        }
    }

}
