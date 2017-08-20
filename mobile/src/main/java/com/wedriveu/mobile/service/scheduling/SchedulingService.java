package com.wedriveu.mobile.service.scheduling;

import com.google.android.gms.location.places.Place;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.util.location.LocationServiceListener;
import com.wedriveu.shared.util.Position;

/**
 * This service interacts with the Vehicle Service Microservice and asks it to find a proper vehicle
 * based on the coordinates given in input.
 *
 * @author Marco Baldassarri
 * @since 20/07/2017
 */
public interface SchedulingService {

    /**
     * Calls the vehicle service and perform a HTTP request to get the chosen vehicle
     *
     * @param destination The GPS coordinates given by the user.
     * @param callback
     */
    void findNearestVehicle(Position userPosition, Place destination, ServiceOperationCallback<Vehicle> callback);

}
