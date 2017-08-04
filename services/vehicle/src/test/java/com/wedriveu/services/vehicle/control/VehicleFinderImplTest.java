package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.nearest.available.boundary.finder.control.VehicleFinderControl;
import com.wedriveu.services.vehicle.nearest.boundary.finder.VehicleFinderImpl;

import org.junit.Before;
import org.junit.Test;
import com.wedriveu.services.shared.utilities.Position;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

public class VehicleFinderImplTest {
    ArrayList<Vehicle> available = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
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
    }

    @Test
    public void listAllEligibleVehiclesOneItem() throws Exception {
        VehicleFinderControl finder = new VehicleFinderImpl();
        finder.listAllEligibleVehicles(new Position(10,10),
                new Position(150,15), available, actual -> {
            assertTrue(actual.size() == 2);
        });
    }

    @Test
    public void listAllEligibleVehiclesNoItem() throws Exception {
        VehicleFinderControl finder = new VehicleFinderImpl();
        finder.listAllEligibleVehicles(new Position(10000,10000),
                new Position(10000,10000), available, actual -> {
            assertTrue(actual.isEmpty());
        });
    }

}