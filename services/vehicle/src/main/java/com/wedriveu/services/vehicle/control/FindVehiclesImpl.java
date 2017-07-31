package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.boundary.CommunicationWithVehicles;
import com.wedriveu.services.vehicle.boundary.CommunicationWithVehiclesImpl;
import com.wedriveu.services.vehicle.callback.FindVehiclesCallback;
import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.shared.utilities.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by stefano.bernagozzi on 17/07/2017.
 */
public class FindVehiclesImpl implements FindVehicles {

    private CommunicationWithVehicles communicationWithVehicles;

    public FindVehiclesImpl() throws IOException, TimeoutException {
        communicationWithVehicles = new CommunicationWithVehiclesImpl();
    }

    public void listAllEligibleVehicles(Position userPosition,
                                        Position destPosition,
                                        List<Vehicle> allAvailable,
                                        FindVehiclesCallback callback) throws IOException {
        CounterVehiclesEligibles counter = new CounterVehiclesEligibles();
        List<Vehicle> eligibles = new ArrayList<>();
        for(Vehicle current: allAvailable) {
            if (isInRange(userPosition, current.getPosition())) {
                double kilometersToDO = (userPosition.getDistance(current.getPosition())) +
                        (userPosition.getDistance(destPosition));
                counter.addCalled();
                communicationWithVehicles.requestCanDoJourney(current.getCarLicencePlate(), kilometersToDO, canDo -> {
                    if (canDo) {
                        eligibles.add(current);
                    }
                    counter.addFinished();
                });
            }
        }
        new Thread(() -> {
            try {
                while(!counter.isFinished()){}
                callback.listAllEligiblesVehiclesCallback(eligibles);
            } catch(Exception v) {
                v.printStackTrace();
            }
        }).start();
    }

    private boolean isInRange(Position userPosition, Position vehiclePosition) {
        return userPosition.getDistance(vehiclePosition) < Constants.RANGE;
    }

}
