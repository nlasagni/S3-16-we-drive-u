package com.wedriveu.services.vehicle.app;

import com.wedriveu.services.vehicle.available.boundary.nearest.NearestConsumerImpl;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) throws IOException, TimeoutException {
       /* VehicleStoreImpl vehicleStore = new VehicleStoreImpl();
        vehicleStore.createVehiclesFile();
        Vehicle vehicleToRetrieve = vehicleStore.getVehicle("MACCHINA1");
        try {
            List<Vehicle> vehiclesAvailable = vehicleStore.getAllAvailableVehicles();
            for(Vehicle vehicle: vehiclesAvailable) {
                Log.log(vehicle.getCarLicencePlate());
            }
            vehicleStore.addVehicle(new Vehicle("MACCHINA6",
                    "available",
                    new Position(11.1,11.1),
                    new Date()));
            Thread.sleep(3000);
            vehicleStore.updateVehicleInVehicleList("MACCHINA1",
                    "recharging",
                    new Position(15.2,18.2),
                    new Date( ));
            Thread.sleep(3000);
            vehicleStore.deleteVehicleFromDb("MACCHINA1");
            Thread.sleep(3000);
            Vehicle dummy =
                    new Vehicle("MACCHINA3",
                            "available",
                            new Position(13.3,13.3),
                            new Date());
            vehicleStore.replaceVehicleInDb("MACCHINA2", dummy);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Vehicle> available = new ArrayList<>();
        available.add(new Vehicle("veicolo1",
                "available",
                new Position(44.139644, 12.246429), //farthest vehicle
                new Date()));
        available.add(new Vehicle("veicolo2", //shortest vehicle
                "available",
                new Position(43.111162, 13.603608),
                new Date()));
        available.add(new Vehicle("veicolo3",
                "available",
                new Position(10, 10),
                new Date()));

        VehicleFinder finder = new VehicleFinderImpl();
        finder.listAllEligibleVehicles( new Position(43.158873, 13.720088),
                new Position(42.960979, 13.874647),
                available,
                new EligibleVehiclesControlImpl());
    }*/

        new NearestConsumerImpl().startVehicleService();
    }

}
