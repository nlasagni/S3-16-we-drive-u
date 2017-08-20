package com.wedriveu.shared.rabbitmq.message;

import com.wedriveu.shared.util.Position;

/**
 * @author Michele Donati on 11/08/2017.
 *
 * This class represents a "book" request that the BookingService sends to the VehicleService.
 */

public class BookVehicleRequest {

    private String username;
    private String licencePlate;
    private Position userPosition;
    private Position destinationPosition;

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookVehicleRequest that = (BookVehicleRequest) o;

        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "BookVehicleRequest{" +
                "username='" + username + '\'' +
                ", licencePlate='" + licencePlate + '\'' +
                ", userPosition=" + userPosition +
                ", destinationPosition=" + destinationPosition +
                '}';
    }
}
