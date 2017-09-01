package com.wedriveu.shared.rabbitmq.message;

/**
 * @author Michele Donati on 11/08/2017.
 *
 * This class represents a "book" request that the service sends to the available vehicles.
 */

public class VehicleReservationRequest {

    private String username;

    /**
     * @return the username that wants to book the vehicle
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username that wants to book the vehicle
     */
    public void setUsername(String username){
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleReservationRequest that = (VehicleReservationRequest) o;

        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "VehicleReservationRequest{" +
                "username='" + username + '\'' +
                '}';
    }

}
