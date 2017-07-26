package com.wedriveu.services.vehicle.election.control;

import com.wedriveu.services.vehicle.election.entity.EligibleVehicle;

import java.util.List;

/**
 * This controller is responsible for choosing the best vehicle to be sent to the user.
 *
 * @author Marco Baldassarri
 * @since 24/07/2017
 */
public interface EligibleVehiclesControl {

    /**
     * Selects the vehicle to send to the user by selecting the vehicle which distance to the user is less then others.
     * This choice is to let the user wait as little as possible.
     *
     * @param vehicleList List of eligible vehicles which can actually drive to the trip destination
     */
    void chooseBestVehicle(List<EligibleVehicle> vehicleList);

}
