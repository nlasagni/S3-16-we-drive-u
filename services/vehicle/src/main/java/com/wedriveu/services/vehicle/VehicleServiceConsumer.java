package com.wedriveu.services.vehicle;

import io.vertx.ext.web.RoutingContext;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Marco Baldassarri
 * @since 30/07/2017
 */
public interface VehicleServiceConsumer {

    void startVehicleService() throws IOException, TimeoutException;

    void stopVehicleService();

    void deployVehicleFinder() throws IOException, TimeoutException;
}
