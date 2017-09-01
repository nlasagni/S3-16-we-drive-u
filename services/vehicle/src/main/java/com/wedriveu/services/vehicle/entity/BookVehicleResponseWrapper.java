package com.wedriveu.services.vehicle.entity;

import com.wedriveu.shared.rabbitmq.message.BookVehicleResponse;
import com.wedriveu.shared.util.Position;

/**
 * This class represents a "book" response wrapper to properly facilitate communication
 * between internal verticles inside the VehicleService once the Vehicle sends the response back.
 *
 * @author Marco on 17/08/2017
 * @author Nicola Lasagni
 */
public class BookVehicleResponseWrapper {

    private BookVehicleResponse response;
    private String username;
    private Position userPosition;
    private Position destinationPosition;

    /**
     * Gets response sent by the vehicle.
     *
     * @return the response
     */
    public BookVehicleResponse getResponse() {
        return response;
    }

    /**
     * Sets response sent by the vehicle.
     *
     * @param response the response
     */
    public void setResponse(BookVehicleResponse response) {
        this.response = response;
    }

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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookVehicleResponseWrapper)) {
            return false;
        }
        BookVehicleResponseWrapper wrapper = (BookVehicleResponseWrapper) o;
        return (response != null ? response.equals(wrapper.response) : wrapper.response == null) &&
                (username != null ? username.equals(wrapper.username) : wrapper.username == null) &&
                (userPosition != null
                        ? userPosition.equals(wrapper.userPosition)
                        : wrapper.userPosition == null) &&
                (destinationPosition != null
                        ? destinationPosition.equals(wrapper.destinationPosition)
                        : wrapper.destinationPosition == null);
    }

    @Override
    public int hashCode() {
        int result = response != null ? response.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (userPosition != null ? userPosition.hashCode() : 0);
        result = 31 * result + (destinationPosition != null ? destinationPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BookVehicleResponseWrapper{" +
                "response=" + response +
                ", username='" + username + '\'' +
                ", userPosition=" + userPosition +
                ", destinationPosition=" + destinationPosition +
                '}';
    }
}
