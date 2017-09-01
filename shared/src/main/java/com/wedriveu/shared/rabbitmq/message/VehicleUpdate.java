package com.wedriveu.shared.rabbitmq.message;

import com.wedriveu.shared.util.Position;

/**
 *  This class represents an "update" command that the vehicle sends to the service.
 *
 * @author Michele Donati on 11/08/2017
 */
public class VehicleUpdate {

    private Position position;
    private String status;
    private String license;
    private String username;
    private String failureMessage;
    private boolean userOnBoard;

    /**
     * Gets position.
     *
     * @return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets position.
     *
     * @param position the position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets license.
     *
     * @return the license
     */
    public String getLicense() {
        return license;
    }

    /**
     * Sets license.
     *
     * @param license the license
     */
    public void setLicense(String license) {
        this.license = license;
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
     * Gets failure message.
     *
     * @return the failure message
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * Sets failure message.
     *
     * @param failureMessage the failure message
     */
    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    /**
     * Is user on board boolean.
     *
     * @return a boolean that indicates if the vehicle has a user on board
     */
    public boolean isUserOnBoard() {
        return userOnBoard;
    }

    /**
     * Sets user on board.
     *
     * @param userOnBoard the boolean that indicates if the vehicle has a user on board
     */
    public void setUserOnBoard(boolean userOnBoard) {
        this.userOnBoard = userOnBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VehicleUpdate)) {
            return false;
        }
        VehicleUpdate that = (VehicleUpdate) o;
        return userOnBoard == that.userOnBoard &&
                (position != null ? position.equals(that.position) : that.position == null) &&
                (status != null ? status.equals(that.status) : that.status == null) &&
                (license != null ? license.equals(that.license) : that.license == null) &&
                (username != null ? username.equals(that.username) : that.username == null) &&
                (failureMessage != null ? failureMessage.equals(that.failureMessage) : that.failureMessage == null);
    }

    @Override
    public int hashCode() {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (license != null ? license.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (failureMessage != null ? failureMessage.hashCode() : 0);
        result = 31 * result + (userOnBoard ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VehicleUpdate{" +
                "position=" + position +
                ", status='" + status + '\'' +
                ", license='" + license + '\'' +
                ", username='" + username + '\'' +
                ", failureMessage='" + failureMessage + '\'' +
                ", userOnBoard=" + userOnBoard +
                '}';
    }
}
