package com.wedriveu.shared.entity;

/**
 * @author Michele Donati on 11/08/2017.
 */

public class VehicleBookRequest {

    private String username;

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

        VehicleBookRequest that = (VehicleBookRequest) o;

        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "VehicleBookRequest{" +
                "username='" + username + '\'' +
                '}';
    }

}
