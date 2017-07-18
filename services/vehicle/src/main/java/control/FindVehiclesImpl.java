package control;

import boundary.CommunicationWithVehicles;
import boundary.CommunicationWithVehiclesImpl;
import entity.Vehicle;
import entity.VehicleStore;
import entity.VehicleStoreImpl;
import utilities.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by stefano.bernagozzi on 17/07/2017.
 */
public class FindVehiclesImpl implements FindVehicles {
    private VehicleStore vehicleStoreReference;
    private CommunicationWithVehicles communicationWithVehicles;
    private double range = 20;
    private double maximumDistanceToRecharge = 20;
    private double estimatedKilometersPerPercentage = 10;

    public FindVehiclesImpl() throws IOException {
        vehicleStoreReference = new VehicleStoreImpl();
        communicationWithVehicles = new CommunicationWithVehiclesImpl();
    }

    public List<Vehicle> listAllEligibleVehicles(Position userPosition, Position destPosition, List<Vehicle> allAvailable) throws IOException {

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

