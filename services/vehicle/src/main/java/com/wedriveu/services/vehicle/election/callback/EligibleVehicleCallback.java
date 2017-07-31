package com.wedriveu.services.vehicle.election.callback;

import com.wedriveu.services.vehicle.entity.Vehicle;

/**
 * Callback to be called whenever the vehicle to serve to the user is ready
 *
 * @author Marco Baldassarri
 * @since 26/07/2017
 */
public interface EligibleVehicleCallback {

    /**
     * Called once the eligible vehicle has been chosen
     * @param vehicle Chosen vehicle to send to the user
     */
    void onEligibleVehicleChosen(Vehicle vehicle);
}
