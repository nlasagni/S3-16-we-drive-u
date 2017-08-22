package com.wedriveu.shared.rabbitmq.message;

/**
 * A change booking request from the vehicle service that has to substitute a vehicle for the user.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public class CompleteBookingRequest {

    private String username;
    private String licensePlate;

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
     * Gets the vehicle license plate.
     *
     * @return the license plate
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Sets the the vehicle license plate.
     *
     * @param licensePlate the license plate
     */
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CompleteBookingRequest)) {
            return false;
        }
        CompleteBookingRequest otherRequest = (CompleteBookingRequest) other;
        return (username != null ? username.equals(otherRequest.username) : otherRequest.username == null) &&
                (licensePlate != null
                        ? licensePlate.equals(otherRequest.licensePlate)
                        : otherRequest.licensePlate == null);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (licensePlate != null ? licensePlate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompleteBookingRequest{" +
                "username='" + username + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                '}';
    }

}
