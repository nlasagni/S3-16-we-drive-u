package com.wedriveu.mobile.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedriveu.mobile.model.Vehicle;
import org.json.JSONObject;

import java.io.IOException;

/**
 * @author Nicola Lasagni on 29/07/2017.
 */
public class VehicleStoreImpl implements VehicleStore {

    private static final String TAG = VehicleStore.class.getSimpleName();
    private static final String VEHICLE_PREFERENCE_NAME = "_vehiclePreferences";
    private static final String VEHICLE_PREFERENCE = "vehicle";

    private SharedPreferences mSharedPreferences;
    private ObjectMapper mObjectMapper;

    public VehicleStoreImpl(Context context) {
        mSharedPreferences = context.getSharedPreferences(VEHICLE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mObjectMapper = new ObjectMapper();
    }

    @Override
    public Vehicle getVehicle() {
        Vehicle vehicle = null;
        try {
            String vehicleJson = mSharedPreferences.getString(VEHICLE_PREFERENCE, JSONObject.NULL.toString());
            vehicle = mObjectMapper.readValue(vehicleJson, Vehicle.class);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred while getting vehicle!", e);
        }
        return vehicle;
    }

    @Override
    public void storeVehicle(Vehicle vehicle) {
        try {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            String vehicleJson = mObjectMapper.writeValueAsString(vehicle);
            editor.putString(VEHICLE_PREFERENCE, vehicleJson);
            editor.apply();
        } catch (JsonProcessingException e) {
            Log.e(TAG, "Error occurred while storing vehicle!", e);
        }
    }

    @Override
    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

}
