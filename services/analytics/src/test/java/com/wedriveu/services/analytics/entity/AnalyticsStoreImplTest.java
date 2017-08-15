package com.wedriveu.services.analytics.entity;


import com.wedriveu.services.shared.entity.AnalyticsVehicle;
import com.wedriveu.services.shared.entity.EntityListStoreStrategy;
import com.wedriveu.services.shared.entity.JsonFileEntityListStoreStrategyImpl;
import com.wedriveu.services.shared.entity.VehicleCounter;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsStoreImplTest {

    private static final String DATABASE_FILE_NAME = AnalyticsStoreImplTest.class.getSimpleName() + ".json";
    private static final String VEHICLE_1_LICENSE_PLATE = "vehicle1";
    private static final String VEHICLE_NOT_FOUND_LICENSE_PLATE = "vehicle3";

    private AnalyticsStore analyticsStore;
    private AnalyticsVehicle vehicle;

    @Before
    public void setUp() throws Exception {
        VehiclesCounterAlgorithm vehiclesCounterAlgorithm = new VehiclesCounterAlgorithmImpl();
        EntityListStoreStrategy<AnalyticsVehicle> storeStrategy =
                new JsonFileEntityListStoreStrategyImpl<>(AnalyticsVehicle.class, DATABASE_FILE_NAME);
        analyticsStore = new AnalyticsStoreImpl(storeStrategy, vehiclesCounterAlgorithm);
        vehicle = new AnalyticsVehicle(VEHICLE_1_LICENSE_PLATE, "available");
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
    public void addVehicle() throws Exception {
        assertTrue(insertVehicleIntoDatabase());
    }

    @Test
    public void updateVehicle() throws Exception {
        boolean insertionSucceded = insertVehicleIntoDatabase();
        final String NEW_STATUS = "broken";
        boolean updateSucceded = analyticsStore.updateVehicle(vehicle.getLicensePlate(), NEW_STATUS);
        Optional<AnalyticsVehicle> vehicleFromStore = analyticsStore.getVehicleByLicensePlate(vehicle.getLicensePlate());
        assertTrue(insertionSucceded &&
                updateSucceded &&
                vehicleFromStore.isPresent() &&
                vehicleFromStore.get().getStatus().equals(NEW_STATUS));

    }

    @Test
    public void getVehicleCounter() throws Exception {
        final int availableVehicles = 5;
        final int brokenVehicles = 6;
        final int bookedVehicles = 7;
        final int stolenVehicles = 8;
        final int rechargingVehicles = 9;
        addSomeVehiclesToDatabase(availableVehicles, brokenVehicles, bookedVehicles, stolenVehicles, rechargingVehicles);
        VehicleCounter counter = analyticsStore.getVehicleCounter();
        assertTrue(counter.getAvailable() == availableVehicles &&
                counter.getBooked() == bookedVehicles &&
                counter.getBroken() == brokenVehicles &&
                counter.getRecharging() == rechargingVehicles &&
                counter.getStolen() == stolenVehicles);

    }


    private void addSomeVehiclesToDatabase(int available, int broken, int booked, int stolen, int recharging) {
        addVehiclesWithStatus(available, "available");
        addVehiclesWithStatus(broken, "broken");
        addVehiclesWithStatus(booked, "booked");
        addVehiclesWithStatus(stolen, "stolen");
        addVehiclesWithStatus(recharging, "recharging");

    }

    private void addVehiclesWithStatus(int numberOfVehicles, String status) {
        for (int i = 0; i < numberOfVehicles; i++) {
            analyticsStore.addVehicle(status + i, status);
        }
    }

    private boolean insertVehicleIntoDatabase() {
        return analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getLicensePlate());
    }
}