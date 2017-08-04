package com.wedriveu.services.vehicle.nearest.control;

import io.vertx.core.eventbus.Message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * This Controller serves all the interaction requests with the
 * {@linkplain com.wedriveu.services.vehicle.entity.VehicleStore} database. It handles the database replies
 * by deploying and interacting with other verticles.
 * <p>
 * It also initialize all the verticles when the app starts.
 *
 * @author Marco Baldassarri
 * @since 02/08/2017
 */
public interface Control {

    void requestAvailableVehicles(Message message);

    void availableVehiclesCompleted(Message message);

    void deployVehicleFinder() throws IOException, TimeoutException;

}
