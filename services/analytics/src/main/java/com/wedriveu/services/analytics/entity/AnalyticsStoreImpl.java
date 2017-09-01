package com.wedriveu.services.analytics.entity;

import com.wedriveu.services.shared.model.AnalyticsVehicle;
import com.wedriveu.services.shared.store.EntityListStoreStrategy;
import com.wedriveu.shared.rabbitmq.message.VehicleCounter;
import com.wedriveu.shared.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;


/**
 * @author Stefano Bernagozzi
 */

public class AnalyticsStoreImpl implements AnalyticsStore {
    private static final String TAG = AnalyticsStoreImpl.class.getSimpleName();
    private static final String ADD_ERROR = "Error while adding vehicle";
    private static final String GET_ERROR = "Error while getting vehicle";
    private static final String UPDATE_ERROR = "Error while updating vehicle";
    private static final String CLEAR_ERROR = "Error while clearing store";
    private EntityListStoreStrategy<AnalyticsVehicle> storeStrategy;
    private VehiclesCounterAlgorithm vehiclesCounterAlgorithmStrategy;

    public AnalyticsStoreImpl(EntityListStoreStrategy<AnalyticsVehicle> storeStrategy,
                              VehiclesCounterAlgorithm vehiclesCounterAlgorithmStrategy) {
        this.storeStrategy = storeStrategy;
        this.vehiclesCounterAlgorithmStrategy = vehiclesCounterAlgorithmStrategy;
    }

    @Override
    public Optional<AnalyticsVehicle> getVehicleByLicensePlate(String licensePlate) {
        try {
            List<AnalyticsVehicle> vehicles = storeStrategy.getEntities();
            Optional<AnalyticsVehicle> vehicle = Optional.empty();
            if (vehicles != null) {
                vehicle = vehicles.stream().filter(b -> b.getLicensePlate().equals(licensePlate)).findFirst();
            }
            return vehicle;
        } catch (Exception e) {
            Log.error(TAG, GET_ERROR, e);
        }
        return Optional.empty();
    }

    @Override
    public boolean addVehicle(String licensePlate, String status) {
        if (licensePlate == null || status == null) {
            return false;
        }
        try {
            List<AnalyticsVehicle> vehicles = storeStrategy.getEntities();
            if (vehicles == null) {
                vehicles = new ArrayList<>();
            }
            vehicles.add(new AnalyticsVehicle(licensePlate, status));
            storeStrategy.storeEntities(vehicles);
            return true;
        } catch (Exception e) {
            Log.error(TAG, ADD_ERROR, e);
        }
        return false;
    }

    @Override
    public boolean updateVehicle(String licensePlate, String status) {
        try {
            List<AnalyticsVehicle> vehicles = storeStrategy.getEntities();
            if (vehicles != null && !vehicles.isEmpty()) {
                IntStream.range(0, vehicles.size()).forEach(i -> {
                    AnalyticsVehicle vehicle = vehicles.get(i);
                    if (vehicle.getLicensePlate().equals(licensePlate)) {
                        vehicle.setStatus(status);
                    }
                });
            }
            storeStrategy.storeEntities(vehicles);
            return true;
        } catch (Exception e) {
            Log.error(TAG, UPDATE_ERROR, e);
        }
        return false;
    }

    @Override
    public void clear() {
        try {
            storeStrategy.clear();
        } catch (Exception e) {
            Log.error(TAG, CLEAR_ERROR, e);
        }
    }

    @Override
    public VehicleCounter getVehicleCounter() {
        try {
            return vehiclesCounterAlgorithmStrategy.vehicleCounter(storeStrategy.getEntities());
        } catch (Exception e) {
            Log.error(TAG, CLEAR_ERROR, e);
        }
        return null;
    }
}
