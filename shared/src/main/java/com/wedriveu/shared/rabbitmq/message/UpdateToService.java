package com.wedriveu.shared.rabbitmq.message;

import com.wedriveu.shared.util.Position;

/**
 * @author Michele Donati on 11/08/2017.
 *
 * This class represents an "update" command that the vehicle sends to the service.
 */

public class UpdateToService {

    private Position position;
    private String status;
    private String license;
    private String username;
    private String failureMessage;
    private boolean userOnBoard;

    /**
     * @return the position updated of the vehicle
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param newPosition the new position of the vehicle
     */
    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    /**
     * @return the new status of the vehicle
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param newStatus the new status of the vehicle
     */
    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    /**
     * @return the license of the vehicle to update
     */
    public String getLicense() {
        return license;
    }

    /**
     * @param newLicense the license of the vehicle to update
     */
    public void setLicense(String newLicense) {
        this.license = newLicense;
    }

    /**
     * @return a failure message in case something's wrong
     */
    public String getFailureMessage() {
        return failureMessage;
    }

    /**
     * @param newFailureMessage a failure message in case something's wrong
     */
    public void setFailureMessage(String newFailureMessage) {
        this.failureMessage = newFailureMessage;
    }

    /**
     * @return a boolean that says if the user is on board
     */
    public boolean getUserOnBoard(){
        return userOnBoard;
    }

    /**
     * @param newUserOnBoard a boolean that says if the user is on board
     */
    public void setUserOnBoard(boolean newUserOnBoard){
        this.userOnBoard = newUserOnBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateToService that = (UpdateToService) o;

        if (!position.equals(that.position)) return false;
        if (!status.equals(that.status)) return false;
        if (!license.equals(that.license)) return false;
        return failureMessage.equals(that.failureMessage);
    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + license.hashCode();
        result = 31 * result + failureMessage.hashCode();
        return result;
    }


    @Override
    public String toString() {
        return "UpdateToService{" +
                "position=" + position +
                ", status='" + status + '\'' +
                ", license='" + license + '\'' +
                ", failureMessage='" + failureMessage + '\'' +
                ", userOnBoard=" + userOnBoard +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
