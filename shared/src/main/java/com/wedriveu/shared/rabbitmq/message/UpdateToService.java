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

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public boolean isUserOnBoard() {
        return userOnBoard;
    }

    public void setUserOnBoard(boolean userOnBoard) {
        this.userOnBoard = userOnBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UpdateToService)) {
            return false;
        }
        UpdateToService that = (UpdateToService) o;
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
        return "UpdateToService{" +
                "position=" + position +
                ", status='" + status + '\'' +
                ", license='" + license + '\'' +
                ", username='" + username + '\'' +
                ", failureMessage='" + failureMessage + '\'' +
                ", userOnBoard=" + userOnBoard +
                '}';
    }
}
