package com.wedriveu.shared.rabbitmq.message;

import com.wedriveu.shared.util.Position;

/**
 * This class represents a vehicle request performed by a client to get an available vehicle.
 *
 * @author Nicola Lasagni on 09/08/2017.
 */
public class VehicleRequest {

    private String username;
    private Position userPosition;
    private Position destinationPosition;

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets user position.
     *
     * @return the user position
     */
    public Position getUserPosition() {
        return userPosition;
    }

    /**
     * Sets user position.
     *
     * @param userPosition the user position
     */
    public void setUserPosition(Position userPosition) {
        this.userPosition = userPosition;
    }

    /**
     * Gets destination position.
     *
     * @return the destination position
     */
    public Position getDestinationPosition() {
        return destinationPosition;
    }

    /**
     * Sets destination position.
     *
     * @param destinationPosition the destination position
     */
    public void setDestinationPosition(Position destinationPosition) {
        this.destinationPosition = destinationPosition;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof VehicleRequest)) {
            return false;
        }
        VehicleRequest otherRequest = (VehicleRequest) other;
        return checkUserName(otherRequest) &&
                checkUserPosition(otherRequest) &&
                checkDestinationPosition(otherRequest);
    }

    private boolean checkUserName(VehicleRequest otherRequest) {
        return username != null
                ? username.equals(otherRequest.username)
                : otherRequest.username == null;
    }

    private boolean checkUserPosition(VehicleRequest otherRequest) {
        return userPosition != null
                ? userPosition.equals(otherRequest.userPosition)
                : otherRequest.userPosition == null;
    }

    private boolean checkDestinationPosition(VehicleRequest otherRequest) {
        return destinationPosition != null
                ? destinationPosition.equals(otherRequest.destinationPosition)
                : otherRequest.destinationPosition == null;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (userPosition != null ? userPosition.hashCode() : 0);
        result = 31 * result + (destinationPosition != null ? destinationPosition.hashCode() : 0);
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
