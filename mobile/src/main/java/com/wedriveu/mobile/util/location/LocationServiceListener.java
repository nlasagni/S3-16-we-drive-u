package com.wedriveu.mobile.util.location;

import android.location.Location;

/**
 * @author Marco Baldassarri on 19/07/2016.
 */
public interface LocationServiceListener {

    /**
     * Method called when the location becomes available
     * @param location
     */
    void onLocationAvailable(Location location);

    /**
     * Method called when the location service is disabled.
     */
    void onLocationServiceDisabled();

}
