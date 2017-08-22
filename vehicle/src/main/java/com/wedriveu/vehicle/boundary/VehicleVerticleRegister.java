package com.wedriveu.vehicle.boundary;

/**
 * @author Michele Donati on 10/08/2017.
 */

/**
 * This interface models the verticle of the vehicle that is used to send the "register" request to the servce from the
 * vehicle.
 */
public interface VehicleVerticleRegister {
    /**
     * This method permits to send the registration request to the service.
     *
     * @param license This indicates the license plate of the vehicle to register in the database.
     */
    void registerToService(String license);

}
