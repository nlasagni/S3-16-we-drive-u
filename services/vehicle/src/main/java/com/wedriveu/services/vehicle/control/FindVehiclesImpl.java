package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.vehicle.boundary.CommunicationWithVehicles;
import com.wedriveu.services.vehicle.boundary.CommunicationWithVehiclesImpl;
import com.wedriveu.services.vehicle.callback.Callback;
import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.entity.VehicleStore;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.shared.utilities.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefano.bernagozzi on 17/07/2017.
 */
public class FindVehiclesImpl implements FindVehicles {

    private VehicleStore vehicleStoreReference;
    private CommunicationWithVehicles communicationWithVehicles;

    public FindVehiclesImpl() throws IOException {
        vehicleStoreReference = new VehicleStoreImpl();
        communicationWithVehicles = new CommunicationWithVehiclesImpl();
    }

    public List<Vehicle> listAllEligibleVehicles(Position userPosition, Position destPosition, List<Vehicle> allAvailable) throws IOException {

        List<Vehicle> eligibles = new ArrayList<>();
        for(Vehicle current: allAvailable) {
            if((isInRange(userPosition, current.getPosition()))) {
                communicationWithVehicles.requestBatteryPercentage(current.getCarLicencePlate(), new Callback() {
                    @Override
                    public void onRequestBatteryPercentage(double percentage) {
                        if(estimateBatteryConsumption(userPosition, destPosition, current.getPosition()) < percentage) {
                            eligibles.add(current);
                        }
                    }
                });
            }
        }
        return eligibles;
    }

    private double estimateBatteryConsumption(Position userPosition, Position destPosition, Position vehiclePosition) {
        return (userPosition.getDistance(vehiclePosition) +
                destPosition.getDistance(userPosition) +
                Util.MAXIMUM_DISTANCE_TO_RECHARGE) / Util.ESTIMATED_KILOMETERS_PER_PERCENTAGE;
    }

    private boolean isInRange(Position userPosition, Position vehiclePosition) {
        return userPosition.getDistance(vehiclePosition) < Util.RANGE;
    }

}

