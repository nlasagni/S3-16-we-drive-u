package com.wedriveu.services.vehicle.election.control;

import com.wedriveu.services.vehicle.entity.EligibleVehicle;

import java.util.List;

/**
 * @author Marco Baldassarri
 * @since 24/07/2017
 */
public interface EligibleVehiclesControl {

    void chooseBestVehicle(List<EligibleVehicle> vehicleList);

}
