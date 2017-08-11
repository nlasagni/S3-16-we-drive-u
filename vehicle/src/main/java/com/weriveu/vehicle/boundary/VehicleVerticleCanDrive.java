package com.weriveu.vehicle.boundary;

import com.wedriveu.shared.entity.CanDriveRequest;
import com.wedriveu.shared.entity.CanDriveResponse;

/**
 * @author Michele Donati on 09/08/2017.
 */
public interface VehicleVerticleCanDrive {

    CanDriveResponse canDrive(CanDriveRequest canDriveRequest);

}
