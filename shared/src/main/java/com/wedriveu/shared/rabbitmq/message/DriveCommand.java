package com.wedriveu.shared.rabbitmq.message;

import com.wedriveu.shared.util.Position;

/**
 * @author Michele Donati on 11/08/2017.
 *
 * This class represents a "drive" command that the service sends to the vehicle.
 */

public class DriveCommand {

    private Position userPosition;
    private Position destinationPosition;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriveCommand that = (DriveCommand) o;

        if (!userPosition.equals(that.userPosition)) return false;
        return destinationPosition.equals(that.destinationPosition);
    }

    @Override
    public int hashCode() {
        int result = userPosition.hashCode();
        result = 31 * result + destinationPosition.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DriveCommand{" +
                "userPosition=" + userPosition +
                ", destinationPosition=" + destinationPosition +
                '}';
    }

}
