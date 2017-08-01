package com.wedriveu.mobile.store;

import com.wedriveu.mobile.model.Vehicle;

/**
 * @author  Nicola Lasagni on 29/07/2017.
 */
public interface VehicleStore {

    /**
     * Fetches the vehicle stored.
     *
     * @return The vehicle stored.
     */
    Vehicle getVehicle();

    /**
     * Stores a vehicle.
     *
     * @param vehicle The vehicle to be stored.
     */
    void storeVehicle(Vehicle vehicle);

    /**
     * Empties the store.
     */
    void clear();

}
