package com.wedriveu.mobile.service.scheduling;

import com.wedriveu.mobile.model.UserLocation;

/**
 * Created by Marco on 18/07/2017.
 */
public interface SchedulingService {

    void findNearestVehicle(UserLocation userLocation);

}
