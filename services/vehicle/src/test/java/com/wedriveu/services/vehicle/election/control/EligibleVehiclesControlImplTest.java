package com.wedriveu.services.vehicle.election.control;

import com.wedriveu.services.shared.utilities.Position;
import com.wedriveu.services.vehicle.entity.Vehicle;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

public class EligibleVehiclesControlImplTest {

    private static final String V1_LICENCE_PLATE = "veicolo1";
    private static final String V2_LICENCE_PLATE = "veicolo2";
    private static final String V3_LICENCE_PLATE = "veicolo3";
    private static final String STATUS = "available";
    private static final double V1_POS_LAT = 44.139644;
    private static final double V1_POS_LON = 12.246429;
    private static final double V2_POS_LAT = 43.111162;
    private static final double V2_POS_LON = 13.603608;
    private static final double V3_POS_LAT = 41.111131;
    private static final double V3_POS_LON = 13.602323;
    private static final double DISTANCE_V1 = 30;
    private static final double DISTANCE_V2 = 20;
    private static final double DISTANCE_V3 = 10;
    /*    private EligibleVehiclesControl control;
        private List<EligibleVehicle> vehicles;*/
    private Vehicle vehicle1;
    private Vehicle vehicle2;
    private Vehicle vehicle3;

    @Before
    public void init() throws IOException {
       /* control = new EligibleVehiclesControlImpl();
        vehicles = new ArrayList<>();*/
        setEligibleVehicleList();
    }

    private void setEligibleVehicleList() {
        vehicle1 = new Vehicle(V1_LICENCE_PLATE,
                STATUS,
                new Position(V1_POS_LAT, V1_POS_LON),
                new Date());
        vehicle2 = new Vehicle(V2_LICENCE_PLATE,
                STATUS,
                new Position(V2_POS_LAT, V2_POS_LON),
                new Date());
        vehicle3 = new Vehicle(V3_LICENCE_PLATE,
                STATUS,
                new Position(V3_POS_LAT, V3_POS_LON),
                new Date());
/*        vehicles.add(new EligibleVehicle(vehicle1, DISTANCE_V1));
        vehicles.add(new EligibleVehicle(vehicle2, DISTANCE_V2));
        vehicles.add(new EligibleVehicle(vehicle3, DISTANCE_V3));*/
    }


    @Test
    public void chooseBestVehicle() throws Exception {
       /* Assert.assertNotNull(control.chooseBestVehicle(vehicles));
        Assert.assertEquals(control.chooseBestVehicle(vehicles), vehicle3);*/
    }


}