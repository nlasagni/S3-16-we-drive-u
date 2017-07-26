package com.wedriveu.services.vehicle.app;

import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.vehicle.election.control.EligibleVehiclesControlImpl;
import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.entity.VehicleStoreImpl;
import com.wedriveu.services.vehicle.finder.control.FindVehicles;
import com.wedriveu.services.vehicle.finder.control.FindVehiclesImpl;

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

        FindVehicles finder = new FindVehiclesImpl();
        finder.listAllEligibleVehicles( new Position(43.158873, 13.720088),
                new Position(42.960979, 13.874647),
                available,
                new EligibleVehiclesControlImpl());
    }

}
