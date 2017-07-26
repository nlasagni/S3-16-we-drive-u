package com.wedriveu.services.vehicle.election.boundary;

import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.election.callback.EligibleVehicleCallback;
import com.wedriveu.services.vehicle.entity.Vehicle;

/**
 * Created by Marco on 26/07/2017.
 */
public class VehicleServiceAPI implements EligibleVehicleCallback {

    @Override
    public void onEligibleVehicleChosen(Vehicle vehicle) {
        Log.log(vehicle.toString());
    }
}
