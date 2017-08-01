package com.wedriveu.services.vehicle.control;

import com.wedriveu.services.vehicle.entity.Vehicle;
import com.wedriveu.services.vehicle.finder.control.FindVehicles;
import com.wedriveu.services.vehicle.finder.control.FindVehiclesImpl;
import com.wedriveu.vehicle.ServerVehicleRabbitMQ;
import org.junit.Before;
import org.junit.Test;
import com.wedriveu.services.shared.utilities.Position;


import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

public class FindVehiclesImplTest {
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
        ServerVehicleRabbitMQ server = new ServerVehicleRabbitMQ("veicolo1", 100.0);
        ServerVehicleRabbitMQ server2 = new ServerVehicleRabbitMQ("veicolo2", 50.0);
        ServerVehicleRabbitMQ server3 = new ServerVehicleRabbitMQ("veicolo3", 1.0);
        server.startVehicleServer();
        server2.startVehicleServer();
        server3.startVehicleServer();
    }

    @Test
    public void listAllEligibleVehiclesOneItem() throws Exception {
        FindVehicles finder = new FindVehiclesImpl();
        finder.listAllEligibleVehicles(new Position(10,10), new Position(150,15), available, actual -> {
            assertTrue(actual.size() == 2);
        });
    }

    @Test
    public void listAllEligibleVehiclesNoItem() throws Exception {
        FindVehicles finder = new FindVehiclesImpl();
        finder.listAllEligibleVehicles(new Position(10000,10000), new Position(10000,10000), available, actual -> {
            assertTrue(actual.isEmpty());
        });
    }

}