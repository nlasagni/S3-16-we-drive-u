package com.wedriveu.services.vehicle.finder.boundary;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Marco Baldassarri
 * @since 30/07/2017
 */
public interface FinderConsumer {

    void startFinderConsumer() throws IOException, TimeoutException;

    void stopVehicleService();




}
