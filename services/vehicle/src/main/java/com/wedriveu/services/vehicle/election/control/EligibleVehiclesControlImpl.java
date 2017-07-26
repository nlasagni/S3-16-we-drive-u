package com.wedriveu.services.vehicle.election.control;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.vehicle.election.boundary.VehicleServiceAPI;
import com.wedriveu.services.vehicle.election.callback.EligibleVehicleCallback;
import com.wedriveu.services.vehicle.election.entity.EligibleVehicle;
import com.wedriveu.services.vehicle.finder.callback.FindVehiclesCallback;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Marco on 24/07/2017.
 */
public class EligibleVehiclesControlImpl implements EligibleVehiclesControl, FindVehiclesCallback {

    private EligibleVehicleCallback callback;

    public EligibleVehiclesControlImpl() {
        this.callback = new VehicleServiceAPI();
    }

    @Override
    public void listAllEligiblesVehiclesCallback(List<EligibleVehicle> eligibles) {
        chooseBestVehicle(eligibles);
    }

    @Override
    public void chooseBestVehicle(List<EligibleVehicle> vehicleList) {
        vehicleList.sort(Comparator.comparing(EligibleVehicle::getDistanceToUser));
        callback.onEligibleVehicleChosen(vehicleList.get(Constants.FIRST_CHOSEN_ELIGIBLE_VEHICLE).getVehicle());
    }

}
