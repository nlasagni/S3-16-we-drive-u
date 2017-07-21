package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.vehicle.boundary.CommunicationWithVehicles;
import com.wedriveu.services.vehicle.boundary.CommunicationWithVehiclesImpl;
import com.wedriveu.services.vehicle.callback.FindVehiclesCallback;
import com.wedriveu.services.vehicle.callback.ListAllEligiblesCallback;
import com.wedriveu.services.vehicle.callback.WaitForAllCallback;
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

    //private VehicleStore vehicleStoreReference;
    private CommunicationWithVehicles communicationWithVehicles;

    public FindVehiclesImpl() throws IOException {
        //vehicleStoreReference = new VehicleStoreImpl();
        communicationWithVehicles = new CommunicationWithVehiclesImpl();
    }

    public void listAllEligibleVehicles(Position userPosition, Position destPosition,
                                                 List<Vehicle> allAvailable, FindVehiclesCallback callback) throws IOException {
        CounterVehiclesEligibles counter = new CounterVehiclesEligibles();
        List<Vehicle> eligibles = new ArrayList<>();
        for(Vehicle current: allAvailable) {
            if (isInRange(userPosition, current.getPosition())) {
                counter.addCalled();
                communicationWithVehicles.requestBatteryPercentage(current.getCarLicencePlate(), percentage -> {
                    if (estimateBatteryConsumption(userPosition, destPosition, current.getPosition()) < percentage) {
                        eligibles.add(current);
                    }
                    counter.addFinished();
                });
            }
        }
        new Thread(() -> {
            try {
                waitForAll(counter, eligibles, callback);
            } catch(Exception v) {
                System.out.println(v);
            }
        }).start();
    }

    private void waitForAll(CounterVehiclesEligibles counter, List<Vehicle> eligibles, FindVehiclesCallback callback) {
        System.out.println("ending");
        System.out.println("prima");
        while(!counter.isFinished()){}
        System.out.println("dopo");
        callback.listAllEligiblesVehiclesCallback(eligibles);
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

