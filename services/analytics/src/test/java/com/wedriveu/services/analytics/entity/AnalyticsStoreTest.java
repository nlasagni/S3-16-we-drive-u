package com.wedriveu.services.analytics.entity;

import com.wedriveu.services.shared.entity.EntityListStoreStrategy;
import com.wedriveu.services.shared.entity.JsonFileEntityListStoreStrategyImpl;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @Author Stefano Bernagozzi
 */


public class AnalyticsStoreTest {
    private static final String DATABASE_FILE_NAME = AnalyticsStoreTest.class.getSimpleName() + ".json";
    private static final String VEHICLE_1_LICENSE_PLATE = "vehicle1";
    private static final String VEHICLE_2_LICENSE_PLATE = "vehicle2";
    private static final String VEHICLE_NOT_FOUND_LICENSE_PLATE = "vehicle3";

    private AnalyticsStore analyticsStore;
    private List<AnalyticsVehicle> vehicles;

    @Before
    public void setUp() throws Exception {
        EntityListStoreStrategy<AnalyticsVehicle> storeStrategy =
                new JsonFileEntityListStoreStrategyImpl<>(AnalyticsVehicle.class, DATABASE_FILE_NAME);
        analyticsStore = new AnalyticsStoreImpl(storeStrategy);
        vehicles = new ArrayList<>();
        vehicles.add(new AnalyticsVehicle(VEHICLE_1_LICENSE_PLATE, "available"));
        vehicles.add(new AnalyticsVehicle(VEHICLE_2_LICENSE_PLATE, "available"));
    }

    @Test
    public void addVehicle() throws Exception {
        AnalyticsVehicle vehicle = vehicles.get(0);
        boolean correctInsertionSuccess = analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getLicensePlate());
        assertTrue(correctInsertionSuccess);
    }

    @Test
    public void getVehicleByLicensePlate() throws Exception {
        AnalyticsVehicle vehicle = vehicles.get(0);
        boolean insertionSucceded = analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getStatus());
        Optional<AnalyticsVehicle> vehicleFromStore = analyticsStore.getVehicleByLicensePlate(vehicle.getLicensePlate());
        Optional<AnalyticsVehicle> vehicleNotFound = analyticsStore.getVehicleByLicensePlate(VEHICLE_NOT_FOUND_LICENSE_PLATE);
        assertTrue(insertionSucceded &&
                vehicleFromStore.isPresent() &&
                !vehicleNotFound.isPresent() &&
                vehicleFromStore.get().getLicensePlate().equals(vehicle.getLicensePlate()));
    }

    @Test
    public void updateVehicle() throws Exception {
        AnalyticsVehicle vehicle = vehicles.get(0);
        boolean insertionSucceded = analyticsStore.addVehicle(vehicle.getLicensePlate(), vehicle.getStatus());
        final String NEW_STATUS = "broken";
        boolean updateSucceded = analyticsStore.updateVehicle(vehicle.getLicensePlate(),NEW_STATUS);
        Optional<AnalyticsVehicle> vehicleFromStore = analyticsStore.getVehicleByLicensePlate(vehicle.getLicensePlate());
        assertTrue(insertionSucceded &&
                updateSucceded &&
                vehicleFromStore.isPresent() &&
                vehicleFromStore.get().getStatus().equals(NEW_STATUS));

    }

}