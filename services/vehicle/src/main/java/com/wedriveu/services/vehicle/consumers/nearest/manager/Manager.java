package com.wedriveu.services.vehicle.consumers.nearest.manager;

import io.vertx.core.eventbus.Message;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Marco Baldassarri
 * @since 02/08/2017
 */
public interface Manager {

    void requestAvailableVehicles(Message message);
    void availableVehiclesCompleted(Message message);
    void deployVehicleFinder() throws IOException, TimeoutException;

}
