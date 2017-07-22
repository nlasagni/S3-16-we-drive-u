import com.wedriveu.services.shared.utilities.Log;
import com.wedriveu.services.vehicle.control.FindVehicles;
import com.wedriveu.services.vehicle.control.FindVehiclesImpl;
import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.shared.utilities.Position;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException {
        VehicleStoreImpl obj = new VehicleStoreImpl();
        obj.mapEntityToJson();
        Vehicle me;
        me = obj.getVehicle("MACCHINA1");
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
