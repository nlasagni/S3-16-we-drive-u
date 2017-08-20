package com.wedriveu.vehicle.boundary;

/**
 * @author Michele Donati on 11/08/2017.
 */

/**
 * This interface models the verticle of the vehicle that is used to send the "update" notifies to the service.
 */
public interface VehicleVerticleUpdate {
    /**
     * This method permits to send the update to the service.
     */
    void sendUpdate();

}
