package com.wedriveu.shared.rabbitmq.message;

/**
 * A change booking request from the vehicle service that has to substitute a vehicle for the user.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public class ChangeBookingRequest {

    private String username;
    private String newLicensePlate;

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
     * Gets the substitution vehicle license plate.
     *
     * @return the license plate
     */
    public String getNewLicensePlate() {
        return newLicensePlate;
    }

    /**
     * Sets the the substitution vehicle license plate.
     *
     * @param licensePlate the license plate
     */
    public void setNewLicensePlate(String licensePlate) {
        this.newLicensePlate = licensePlate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChangeBookingRequest)) {
            return false;
        }
        ChangeBookingRequest that = (ChangeBookingRequest) o;
        return (username != null ? username.equals(that.username) : that.username == null) &&
                (newLicensePlate != null ? newLicensePlate.equals(that.newLicensePlate) : that.newLicensePlate == null);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (newLicensePlate != null ? newLicensePlate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChangeBookingRequest{" +
                "username='" + username + '\'' +
                ", newLicensePlate='" + newLicensePlate + '\'' +
                '}';
    }

}
