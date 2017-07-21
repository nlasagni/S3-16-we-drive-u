package com.wedriveu.mobile.util.location;

import android.content.Intent;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by Nicola Lasagni on 19/07/2016.
 */
public interface LocationService {

    String TAG = LocationService.class.getSimpleName();

    int PERMISSION_REQUEST = 0;
    int CHECK_SETTINGS_REQUEST = 1;
    int LOCATION_REQUEST_INTERVAL = 10000;
    int LOCATION_REQUEST_FASTEST_INTERVAL = 5000;
    int LOCATION_REQUEST_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

    Location getLastKnownLocation();

    /**
     * Checks for needed settings change.
     */
    void checkSettings();

    /**
     * Disables location and resets the last known position.
     */
    void disableLocationService();

    /**
     * This method must be called from the main Activity to handle Settings change.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * This method must be called from the main Activity to handle requestPermission call.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void addLocationListener(LocationServiceListener listener);

    void removeLocationListener(LocationServiceListener listener);

}
