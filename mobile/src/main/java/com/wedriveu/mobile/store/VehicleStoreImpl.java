package com.wedriveu.mobile.store;

import android.content.Context;
import android.content.SharedPreferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.mobile.model.Vehicle;

/**
 * @author Nicola Lasagni on 29/07/2017.
 */
public class VehicleStoreImpl implements VehicleStore {

    private static final String TAG = UserStore.class.getSimpleName();
    private static final String VEHICLE_PREFERENCE_NAME = "_vehiclePreferences";
    private static final String VEHICLE_PREFERENCE = "vehicle";
    private SharedPreferences mSharedPreferences;
    private ObjectMapper mObjectMapper;

    public VehicleStoreImpl(Context context) {

    }

    @Override
    public Vehicle getVehicle() {
        return null;
    }

    @Override
    public void storeVehicle(Vehicle vehicle) {}

    @Override
    public void clear() {}

}
