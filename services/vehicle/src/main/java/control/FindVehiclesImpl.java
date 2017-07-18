package control;

import boundary.CommunicationWithVehicles;
import database.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefano.bernagozzi on 17/07/2017.
 */
public class FindVehiclesImpl {
    private VehicleStore vehicleStoreReference;
    private CommunicationWithVehicles communicationWithVehicles;
    private double range = 0.5;
    private double maximumDistanceToRecharge = 20;
    private double estimatedKilometersPerPercentage = 20;

    public FindVehiclesImpl() {
        vehicleStoreReference = new VehicleStoreImpl();
    }

    public List<Vehicle> listAllEligibleVehicles(Position userPosition, Position destPosition, List<Vehicle> allAvailable) {

        List<Vehicle> eligibles = new ArrayList<>();
        for(Vehicle current: allAvailable) {
            if( (isInRange(userPosition, current.getPosition())) ){
                double percentage = communicationWithVehicles.requestBatteryPercentage(current.getCarLicencePlate());
                if(estimateBatteryConsumption(userPosition, destPosition, current.getPosition()) < percentage) {
                    eligibles.add(current);
                }
            }
        }
        return eligibles;

    }

    private double estimateBatteryConsumption(Position userPosition, Position destPosition, Position vehiclePosition) {
        return (userPosition.getDistance(vehiclePosition) +
                destPosition.getDistance(userPosition) + maximumDistanceToRecharge) / estimatedKilometersPerPercentage;
    }

    private boolean isInRange(Position userPosition, Position vehiclePosition) {
        return userPosition.getDistance(vehiclePosition) < range;
    }
}

