package com.wedriveu.services.analytics.entity;

import com.wedriveu.services.shared.entity.EntityListStoreStrategy;
import com.wedriveu.services.shared.entity.JsonFileEntityListStoreStrategyImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Stefano Bernagozzi
 */


public class AnalyticsStoreTest {
    private static final String DATABASE_FILE_NAME = AnalyticsStoreTest.class.getSimpleName() + ".json";
    private static final String VEHICLE_1_LICENSE_PLATE = "vehicle1";
    private static final String VEHICLE_NOT_FOUND_LICENSE_PLATE = "vehicle3";

    private AnalyticsStore analyticsStore;
    private AnalyticsVehicle vehicle;

    @Before
    public void setUp() throws Exception {
        EntityListStoreStrategy<AnalyticsVehicle> storeStrategy =
                new JsonFileEntityListStoreStrategyImpl<>(AnalyticsVehicle.class, DATABASE_FILE_NAME);
        analyticsStore = new AnalyticsStoreImpl(storeStrategy);
        vehicle= new AnalyticsVehicle(VEHICLE_1_LICENSE_PLATE, "available");
    }

    @Test
    public void addVehicle() throws Exception {
        boolean correctInsertionSuccess = insertVehicleIntoDatabase();
        assertTrue(correctInsertionSuccess);
    }

    @Test
    public void getVehicleByLicensePlate() throws Exception {
        boolean insertionSucceded = insertVehicleIntoDatabase();
        Optional<AnalyticsVehicle> vehicleFromStore = analyticsStore.getVehicleByLicensePlate(vehicle.getLicensePlate());
        Optional<AnalyticsVehicle> vehicleNotFound = analyticsStore.getVehicleByLicensePlate(VEHICLE_NOT_FOUND_LICENSE_PLATE);
        assertTrue(insertionSucceded &&
                vehicleFromStore.isPresent() &&
                !vehicleNotFound.isPresent() &&
                vehicleFromStore.get().getLicensePlate().equals(vehicle.getLicensePlate()));
    }

    @Test
    public void updateVehicle() throws Exception {
        boolean insertionSucceded = insertVehicleIntoDatabase();
        final String NEW_STATUS = "broken";
        boolean updateSucceded = analyticsStore.updateVehicle(vehicle.getLicensePlate(),NEW_STATUS);
        Optional<AnalyticsVehicle> vehicleFromStore = analyticsStore.getVehicleByLicensePlate(vehicle.getLicensePlate());
        assertTrue(insertionSucceded &&
                updateSucceded &&
                vehicleFromStore.isPresent() &&
                vehicleFromStore.get().getStatus().equals(NEW_STATUS));

    }

    private boolean insertVehicleIntoDatabase() {
        return analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getLicensePlate());
    }
}