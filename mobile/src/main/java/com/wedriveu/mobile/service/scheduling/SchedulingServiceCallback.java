package com.wedriveu.mobile.service.scheduling;

import com.wedriveu.mobile.model.Vehicle;

/**
 * Created by Marco on 18/07/2017.
 */
public interface SchedulingServiceCallback {
    void onFindNearestVehicleFinished(Vehicle vehicle);
}
