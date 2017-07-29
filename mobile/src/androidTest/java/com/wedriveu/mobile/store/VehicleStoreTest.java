package com.wedriveu.mobile.store;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import com.wedriveu.mobile.model.Vehicle;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author  Nicola Lasagni on 29/07/2017.
 */
public class VehicleStoreTest {

    private static final String DUMMY_VEHICLE_NAME = "Mini Cooper";
    private static final String DUMMY_VEHICLE_LICENSE_PLATE = "ABCDEXYZ";
    private static final String DUMMY_VEHICLE_DESCRIPTION = "A super fast car!";

    private VehicleStore mVehicleStore;

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getTargetContext();
        mVehicleStore = new VehicleStoreImpl(context);
    }

    @Test
    public void getVehicle() throws Exception {
        mVehicleStore.storeVehicle(createDummyVehicle());
        assertTrue(mVehicleStore.getVehicle() != null);
    }

    @Test
    public void storeVehicle() throws Exception {
        Vehicle vehicle = createDummyVehicle();
        mVehicleStore.storeVehicle(vehicle);
        Vehicle storedVehicle = mVehicleStore.getVehicle();
        assertTrue(storedVehicle != null && storedVehicle.getLicencePlate().equals(vehicle.getLicencePlate()));
    }

    private Vehicle createDummyVehicle() {
        Vehicle vehicle =
                new Vehicle(DUMMY_VEHICLE_LICENSE_PLATE,
                        DUMMY_VEHICLE_NAME,
                        DUMMY_VEHICLE_DESCRIPTION,
                        null,
                        null,
                        null);
        return vehicle;
    }

}