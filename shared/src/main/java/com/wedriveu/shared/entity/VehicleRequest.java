package com.wedriveu.shared.entity;

/**
 * @author Nicola Lasagni on 09/08/2017.
 */
public class VehicleRequest {

    private String username;
    private Position userPosition;
    private Position destinationPosition;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Position getUserPosition() {
        return userPosition;
    }

    public void setUserPosition(Position userPosition) {
        this.userPosition = userPosition;
    }

    public Position getDestinationPosition() {
        return destinationPosition;
    }

    public void setDestinationPosition(Position destinationPosition) {
        this.destinationPosition = destinationPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleRequest)) return false;

        VehicleRequest that = (VehicleRequest) o;

        return (getUsername() != null ? getUsername().equals(that.getUsername()) : that.getUsername() == null) &&
                (getUserPosition() != null ? getUserPosition().equals(that.getUserPosition()) : that.getUserPosition() == null) &&
                (getDestinationPosition() != null ? getDestinationPosition().equals(that.getDestinationPosition()) : that.getDestinationPosition() == null);
    }

    @Override
    public int hashCode() {
        int result = getUsername() != null ? getUsername().hashCode() : 0;
        result = 31 * result + (getUserPosition() != null ? getUserPosition().hashCode() : 0);
        result = 31 * result + (getDestinationPosition() != null ? getDestinationPosition().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleRequest{" +
                "username='" + username + '\'' +
                ", userPosition=" + (userPosition != null ? userPosition.toString() : "") +
                ", destinationPosition=" + (destinationPosition != null ? destinationPosition.toString() : "") +
                '}';
    }

}
