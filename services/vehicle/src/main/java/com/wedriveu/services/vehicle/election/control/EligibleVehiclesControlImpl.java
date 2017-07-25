package com.wedriveu.services.vehicle.election.control;

import com.wedriveu.services.vehicle.entity.EligibleVehicle;
import com.wedriveu.services.vehicle.finder.callback.FindVehiclesCallback;

import java.util.List;

/**
 * Created by Marco on 24/07/2017.
 */
public class EligibleVehiclesControlImpl implements EligibleVehiclesControl, FindVehiclesCallback {


    @Override
    public void listAllEligiblesVehiclesCallback(List<EligibleVehicle> eligibles) {
        chooseBestVehicle(eligibles);
    }

    @Override
    public void chooseBestVehicle(List<EligibleVehicle> vehicleList) {

            /* the kilometers the vehicle has to make in order
                        to drive the User to the chosen destination have to be the shorter*/

    }

}
