package com.wedriveu.shared.rabbitmq.message;

import com.wedriveu.shared.util.Position;

/**
 * A find position booking response which containte the positions of a booking.
 *
 * @author Nicola Lasagni on 12/08/2017.
 */
public class FindBookingPositionsResponse {

    private boolean successful;
    private String username;
    private String licensePlate;
    private Position userPosition;
    private Position destinationPosition;

    /**
     * Is successful boolean.
     *
     * @return the boolean
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * Sets successful.
     *
     * @param successful the successful
     */
    public void setSuccessful(boolean successful) {
        this.successful = successful;
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
     * Gets vehicle license plate.
     *
     * @return the license plate
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Sets vehicle license plate.
     *
     * @param licensePlate the license plate
     */
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
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
     * Gets booking destination position.
     *
     * @return the booking destination position
     */
    public Position getDestinationPosition() {
        return destinationPosition;
    }

    /**
     * Sets booking destination position.
     *
     * @param destinationPosition the booking destination position
     */
    public void setDestinationPosition(Position destinationPosition) {
        this.destinationPosition = destinationPosition;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FindBookingPositionsResponse)) {
            return false;
        }
        FindBookingPositionsResponse that = (FindBookingPositionsResponse) o;
        return successful == that.successful &&
                (username != null ? username.equals(that.username) : that.username == null) &&
                (licensePlate != null ? licensePlate.equals(that.licensePlate) : that.licensePlate == null) &&
                (userPosition != null ? userPosition.equals(that.userPosition) : that.userPosition == null) &&
                (destinationPosition != null
                        ? destinationPosition.equals(that.destinationPosition)
                        : that.destinationPosition == null);
    }

    @Override
    public int hashCode() {
        int result = (successful ? 1 : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (licensePlate != null ? licensePlate.hashCode() : 0);
        result = 31 * result + (userPosition != null ? userPosition.hashCode() : 0);
        result = 31 * result + (destinationPosition != null ? destinationPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FindBookingPositionsResponse{" +
                "successful=" + successful +
                ", username='" + username + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", userPosition=" + userPosition +
                ", destinationPosition=" + destinationPosition +
                '}';
    }
}
