package com.wedriveu.vehicle.boundary;

/**
 * @author Michele Donati on 11/08/2017.
 */

/**
 * This interface models the verticle of the vehicle that is used to send the "Arrived to destination" notifies to the
 * service.
 */
public interface VehicleVerticleArrivedNotify {
    /**
     * This method permits to send the notify "Arrived to destination" to the service.
     */
    void sendArrivedNotify();
}
