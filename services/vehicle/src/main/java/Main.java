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
                new Position(400,400),
                new Date()));
        available.add(new Vehicle("veicolo3",
                "available",
                new Position(600,600),
                new Date()));
        FindVehicles finder = new FindVehiclesImpl();
        ArrayList<Vehicle> actual =
                (ArrayList<Vehicle>) finder.listAllEligibleVehicles(new Position(10,10),
                        new Position(15,15), available);

        for(Vehicle vehicle: actual){
            System.out.println(vehicle.getCarLicencePlate());
        }

    }

}
