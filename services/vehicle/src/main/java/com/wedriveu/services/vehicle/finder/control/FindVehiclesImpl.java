package com.wedriveu.services.vehicle.finder.control;

import com.wedriveu.services.shared.utilities.Constants;
import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.vehicle.entity.EligibleVehicle;
import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.finder.boundary.CommunicationWithVehicles;
import com.wedriveu.services.vehicle.finder.boundary.CommunicationWithVehiclesImpl;
import com.wedriveu.services.vehicle.finder.callback.FindVehiclesCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefano.bernagozzi on 17/07/2017.
 */
public class FindVehiclesImpl implements FindVehicles {

    private CommunicationWithVehicles communicationWithVehicles;
    private double distanceToDestination;
    private double distanceToUser;

    public FindVehiclesImpl() throws IOException {
        communicationWithVehicles = new CommunicationWithVehiclesImpl();
    }

    public void listAllEligibleVehicles(Position userPosition,
                                        Position destPosition,
                                        List<Vehicle> allAvailable,
                                        FindVehiclesCallback callback) throws IOException {
        CounterVehiclesEligibles counter = new CounterVehiclesEligibles();
        List<EligibleVehicle> eligibles = new ArrayList<>();
        for (Vehicle current : allAvailable) {
            if (isInRange(userPosition, current.getPosition())) {
                distanceToUser = userPosition.getDistance(current.getPosition());
                distanceToDestination = (distanceToUser) + (userPosition.getDistance(destPosition));
                counter.addCalled();
                communicationWithVehicles.requestCanDoJourney(current.getCarLicencePlate(), distanceToDestination, canDo -> {
                    if (canDo) {
                        eligibles.add(new EligibleVehicle(current, distanceToDestination, distanceToUser));
                    }
                    counter.addFinished();
                });
            }
        }
        new Thread(() -> {
            try {
                while (!counter.isFinished()) {
                }
                callback.listAllEligiblesVehiclesCallback(eligibles);
            } catch (Exception v) {
                v.printStackTrace();
            }
        }).start();
    }

    private boolean isInRange(Position userPosition, Position vehiclePosition) {
        return userPosition.getDistance(vehiclePosition) < Constants.RANGE;
    }

}

