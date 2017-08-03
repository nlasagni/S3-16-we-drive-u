package com.wedriveu.services.vehicle.available.control;

import io.vertx.core.eventbus.Message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Marco Baldassarri
 * @since 02/08/2017
 */
public interface AvailableControl {

    void requestAvailableVehicles(Message message);
    void availableVehiclesCompleted(Message message);
    void deployVehicleFinder() throws IOException, TimeoutException;

}
