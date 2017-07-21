package com.wedriveu.mobile.service.scheduling;

import com.google.android.gms.location.places.Place;
import com.wedriveu.mobile.util.location.LocationServiceListener;

/**
 * This service interacts with the VehicleService Microservice and asks it to find a proper vehicle based on the coordinates
 * given in input.
 *
 * @author Marco Baldassarri
 * @since 20/07/2017
 */
public interface SchedulingService extends LocationServiceListener {

    String TAG = SchedulingService.class.getSimpleName();

    /**
     * <p>
     *     Calls the vehicle service and perform a HTTP request to get the chosen vehicle
     * </p>
     * @param address The GPS coordinates given by the user.
     * @param callback
     */
    void findNearestVehicle(Place address, SchedulingServiceCallback callback);

}
