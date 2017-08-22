package com.wedriveu.shared.rabbitmq.message;

import com.wedriveu.shared.util.Position;

/**
 * @author Michele Donati on 11/08/2017.
 *
 * This class represents a "drive" command that the service sends to the vehicle.
 */

public class DriveCommand {

    private String licensePlate;
    private Position userPosition;
    private Position destinationPosition;

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Position getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(Position newUserPosition) {
        this.userPosition = newUserPosition;
    }

    public Position getDestinationPosition() {
        return destinationPosition;
    }

    public void setDestinationPosition(Position newDestinationPosition) {
        this.destinationPosition = newDestinationPosition;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DriveCommand)) {
            return false;
        }
        DriveCommand that = (DriveCommand) o;
        return (licensePlate != null ? licensePlate.equals(that.licensePlate) : that.licensePlate == null) &&
                (userPosition != null ? userPosition.equals(that.userPosition) : that.userPosition == null) &&
                (destinationPosition != null
                        ? destinationPosition.equals(that.destinationPosition)
                        : that.destinationPosition == null);
    }

    @Override
    public int hashCode() {
        int result = licensePlate != null ? licensePlate.hashCode() : 0;
        result = 31 * result + (userPosition != null ? userPosition.hashCode() : 0);
        result = 31 * result + (destinationPosition != null ? destinationPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DriveCommand{" +
                "licensePlate='" + licensePlate + '\'' +
                ", userPosition=" + userPosition +
                ", destinationPosition=" + destinationPosition +
                '}';
    }
}
