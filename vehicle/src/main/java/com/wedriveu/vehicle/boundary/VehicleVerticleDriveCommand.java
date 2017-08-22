package com.wedriveu.vehicle.boundary;

import com.wedriveu.shared.rabbitmq.message.DriveCommand;

/**
 * @author Michele Donati on 11/08/2017.
 */

/**
 * This interface models the verticle of the vehicle that is used to send the "drive" command to a vehicle.
 */
public interface VehicleVerticleDriveCommand {
    /**
     * This method permits to command a vehicle to start driving.
     *
     * @param driveCommand Thid indicates the command object that contains the coordinates to reach.
     */
    void drive(DriveCommand driveCommand);

}
