package com.wedriveu.mobile.util.location;

import android.location.Location;

/**
 * Created by Nicola Lasagni on 19/07/2016.
 */
public interface LocationServiceListener {

    void onLocationAvailable(Location location);

    void onLocationServiceDisabled();

}
