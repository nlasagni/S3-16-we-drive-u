package com.wedriveu.mobile.service.scheduling;

import com.wedriveu.mobile.model.Vehicle;

/**
 * <p>
 *     SchedulingServiceCallback
 * </p>
 * @author Marco Baldassarri
 * @since 20/07/2017
 */
public interface SchedulingServiceCallback {
    /**
     * <p>
     *     Method called when the VehicleService API responds to the user.
     * </p>
     * @param vehicle Object returned after the HTTP Rest interaction. Represents the vehicle object.
     * @param errorMessage The error message returned from the VehicleService.
     */
    void onFindNearestVehicleFinished(Vehicle vehicle, String errorMessage);

}
