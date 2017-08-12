package com.wedriveu.shared.entity;

import com.wedriveu.shared.utils.Position;

/**
 * @author Michele Donati on 11/08/2017.
 *
 * This class represents an "update" command that the vehicle sends to the service.
 */

public class UpdateToService {

    private Position position;
    private String status;
    private String license;
    private String failureMessage;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String newLicense) {
        this.license = newLicense;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(String newFailureMessage) {
        this.failureMessage = newFailureMessage;
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
                '}';
    }

}
