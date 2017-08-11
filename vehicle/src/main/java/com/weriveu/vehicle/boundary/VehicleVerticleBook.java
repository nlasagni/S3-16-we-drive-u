package com.weriveu.vehicle.boundary;

import com.wedriveu.shared.entity.VehicleBookRequest;
import com.wedriveu.shared.entity.VehicleBookResponse;

/**
 * @author Michele Donati on 11/08/2017.
 */

public interface VehicleVerticleBook {

    public VehicleBookResponse book(VehicleBookRequest request);

}
