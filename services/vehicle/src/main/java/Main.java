import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.control.FindVehicles;
import com.wedriveu.services.vehicle.control.FindVehiclesImpl;
import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.shared.utilities.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        VehicleStoreImpl obj = new VehicleStoreImpl();
        obj.createVehiclesFile();
        Vehicle me;
        me = obj.getVehicle("MACCHINA1");
        try {
            List<Vehicle> vehiclesAvailable = obj.getAllAvailableVehicles();
            for(Vehicle vehicle: vehiclesAvailable) {
                Log.log(vehicle.getCarLicencePlate());
            }
            obj.addVehicle(new Vehicle("MACCHINA6",
                    "available",
                    new Position(11.1,11.1),
                    new Date()));
            Thread.sleep(3000);
            obj.updateVehicleInVehicleList("MACCHINA1",
                    "recharging",
                    new Position(15.2,18.2),
                    new Date( ));
            Thread.sleep(3000);
            obj.deleteVehicleFromDb("MACCHINA1");
            Thread.sleep(3000);
            Vehicle dummy =
                    new Vehicle("MACCHINA3",
                            "available",
                            new Position(13.3,13.3),
                            new Date());
            obj.replaceVehicleInDb("MACCHINA2", dummy);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayList<Vehicle> available = new ArrayList<>();
        available.add(new Vehicle("veicolo1",
                "available",
                new Position(10,10),
                new Date()));
        available.add(new Vehicle("veicolo2",
                "available",
                new Position(10,10),
                new Date()));
        available.add(new Vehicle("veicolo3",
                "available",
                new Position(10,10),
                new Date()));

        FindVehicles finder = new FindVehiclesImpl();
        finder.listAllEligibleVehicles(new Position(10,10),
                new Position(15,15), available,
                actual -> {
                    Log.log("printing vehicles list");
                    for(Vehicle vehicle: actual){
                        Log.log(vehicle.getCarLicencePlate());
                    }
                });
    }

}
