package com.wedriveu.services.vehicle.available.boundary;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Marco Baldassarri
 * @since 30/07/2017
 */
public interface AvailableConsumer {

    void startVehicleService() throws IOException, TimeoutException;

    void stopVehicleService();




}
