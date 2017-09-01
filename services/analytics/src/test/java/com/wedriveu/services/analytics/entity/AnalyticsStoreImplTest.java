package com.wedriveu.services.analytics.entity;


import com.wedriveu.services.shared.model.AnalyticsVehicle;
import com.wedriveu.services.shared.store.EntityListStoreStrategy;
import com.wedriveu.services.shared.store.JsonFileEntityListStoreStrategyImpl;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import com.wedriveu.shared.util.Constants;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * @author Stefano Bernagozzi
 */
public class AnalyticsStoreImplTest {

    private static final String DATABASE_FILE_NAME = AnalyticsStoreImplTest.class.getSimpleName()  + ".json";
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
        analyticsStore.clear();
        vehicle = new AnalyticsVehicle(VEHICLE_1_LICENSE_PLATE, Constants.Vehicle.STATUS_AVAILABLE);
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
        final String NEW_STATUS = Constants.Vehicle.STATUS_BROKEN_STOLEN;
        boolean updateSucceded = analyticsStore.updateVehicle(vehicle.getLicensePlate(), NEW_STATUS);
        Optional<AnalyticsVehicle> vehicleFromStore = analyticsStore.getVehicleByLicensePlate(vehicle.getLicensePlate());
        assertTrue(insertionSucceded &&
                updateSucceded &&
                vehicleFromStore.isPresent() &&
                vehicleFromStore.get().getStatus().equals(NEW_STATUS));

    }

    @Test
    public void getVehicleCounter() throws Exception {
        analyticsStore.clear();
        final int availableVehicles = 5;
        final int brokenVehicles = 6;
        final int bookedVehicles = 7;
        final int stolenVehicles = 8;
        final int rechargingVehicles = 9;
        addSomeVehiclesToDatabase(availableVehicles, brokenVehicles, bookedVehicles, stolenVehicles, rechargingVehicles);
        VehicleCounter counter = analyticsStore.getVehicleCounter();
        assertTrue(counter.getAvailable() == availableVehicles &&
                counter.getBooked() == bookedVehicles &&
                counter.getBrokenStolen() == brokenVehicles &&
                counter.getRecharging() == rechargingVehicles &&
                counter.getNetworkIssues() == stolenVehicles);

    }


    private void addSomeVehiclesToDatabase(int available, int broken, int booked, int networkIssues, int recharging) {
        addVehiclesWithStatus(available, Constants.Vehicle.STATUS_AVAILABLE);
        addVehiclesWithStatus(broken, Constants.Vehicle.STATUS_BROKEN_STOLEN);
        addVehiclesWithStatus(booked, Constants.Vehicle.STATUS_BOOKED);
        addVehiclesWithStatus(networkIssues, Constants.Vehicle.STATUS_NETWORK_ISSUES);
        addVehiclesWithStatus(recharging, Constants.Vehicle.STATUS_RECHARGING);

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