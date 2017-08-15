package com.wedriveu.mobile.util.location;

import android.content.Intent;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;

/**
 * Handles the user location based on the phone GPS module.
 * @author Marco Baldassarri
 *
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
     * @param requestCode Activity request code
     * @param resultCode Activity result code
     * @param data Data to pass to the View Model Fragments in order to handle the view
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * This method must be called from the main Activity to handle requestPermission call.
     * @param requestCode Activity request code
     * @param permissions Array of user permissions requested by the app
     * @param grantResults Array of results based on the user permission choices
     */
    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    /**
     * Method to add the listener in order to the user location changes
     * @param listener The listener passed to handle the location changes
     */
    void addLocationListener(LocationServiceListener listener);

    void removeLocationListener(LocationServiceListener listener);

}
