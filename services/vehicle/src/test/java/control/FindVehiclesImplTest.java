package control;

import boundary.CommunicationWithVehicles;
import entity.Vehicle;
import org.junit.Before;
import org.junit.Test;
import utilities.Position;


import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.*;

public class FindVehiclesImplTest {
    ArrayList<Vehicle> available = new ArrayList<>();
    @Before
    public void setUp() throws Exception {
        available.add(new Vehicle("veicolo1", "available", new Position(10,10), new Date()));
        available.add(new Vehicle("veicolo2", "available", new Position(400,400), new Date()));
        available.add(new Vehicle("veicolo3", "available", new Position(600,600), new Date()));
    }

    @Test
    public void listAllEligibleVehiclesOneItem() throws Exception {
        FindVehicles finder = new FindVehiclesImpl();
        ArrayList<Vehicle> actual = (ArrayList<Vehicle>) finder.listAllEligibleVehicles(new Position(10,10), new Position(150,15), available);
        assertTrue(actual.size() == 1);
    }

    @Test
    public void listAllEligibleVehiclesNoItem() throws Exception {
        FindVehicles finder = new FindVehiclesImpl();
        ArrayList<Vehicle> actual = (ArrayList<Vehicle>) finder.listAllEligibleVehicles(new Position(10,10), new Position(10000,10000), available);
        assertTrue(actual.isEmpty());
    }

}