package com.wedriveu.shared.rabbitmq.message;

/**
 * A find position booking request to find the positions of a booking.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public class FindBookingPositionsRequest {

    private String username;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FindBookingPositionsRequest)) {
            return false;
        }
        FindBookingPositionsRequest that = (FindBookingPositionsRequest) o;
        return username != null ? username.equals(that.username) : that.username == null;
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FindBookingPositionsRequest{" +
                "username='" + username + '\'' +
                '}';
    }
}
