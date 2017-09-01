package com.wedriveu.services.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.rabbitmq.message.VehicleUpdate;
import com.wedriveu.shared.util.Position;

/**
 * Represents a substitution check used when a vehicle notifies this service with a "broken" status.
 *
 * @author Nicola Lasagni on 25/08/2017.
 */
public class SubstitutionCheck {

    private String checkerId;
    private boolean needed;
    private VehicleUpdate vehicleUpdate;
    private Position sourcePosition;
    private Position destinationPosition;

    /**
     * Instantiates a new Substitution check.
     *
     * @param checkerId           the checker id
     * @param needed              the needed
     * @param vehicleUpdate     the update to service
     * @param sourcePosition      the source position
     * @param destinationPosition the destination position
     */
    public SubstitutionCheck(@JsonProperty("checkerId") String checkerId,
                             @JsonProperty("needed") boolean needed,
                             @JsonProperty("updateToService") VehicleUpdate vehicleUpdate,
                             @JsonProperty("sourcePosition") Position sourcePosition,
                             @JsonProperty("destinationPosition") Position destinationPosition) {
        this.checkerId = checkerId;
        this.needed = needed;
        this.vehicleUpdate = vehicleUpdate;
        this.sourcePosition = sourcePosition;
        this.destinationPosition = destinationPosition;
    }

    /**
     * Gets checker id.
     *
     * @return the checker id
     */
    public String getCheckerId() {
        return checkerId;
    }

    /**
     * Is needed boolean.
     *
     * @return the boolean
     */
    public boolean isNeeded() {
        return needed;
    }

    /**
     * Gets update to service.
     *
     * @return the update to service
     */
    public VehicleUpdate getVehicleUpdate() {
        return vehicleUpdate;
    }

    /**
     * Gets source position.
     *
     * @return the source position
     */
    public Position getSourcePosition() {
        return sourcePosition;
    }

    /**
     * Gets destination position.
     *
     * @return the destination position
     */
    public Position getDestinationPosition() {
        return destinationPosition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SubstitutionCheck)) {
            return false;
        }
        SubstitutionCheck that = (SubstitutionCheck) o;
        return needed == that.needed &&
                (checkerId != null ? checkerId.equals(that.checkerId) : that.checkerId == null) &&
                (vehicleUpdate != null
                        ? vehicleUpdate.equals(that.vehicleUpdate)
                        : that.vehicleUpdate == null) &&
                (sourcePosition != null
                        ? sourcePosition.equals(that.sourcePosition)
                        : that.sourcePosition == null) &&
                (destinationPosition != null
                        ? destinationPosition.equals(that.destinationPosition)
                        : that.destinationPosition == null);
    }

    @Override
    public int hashCode() {
        int result = checkerId != null ? checkerId.hashCode() : 0;
        result = 31 * result + (needed ? 1 : 0);
        result = 31 * result + (vehicleUpdate != null ? vehicleUpdate.hashCode() : 0);
        result = 31 * result + (sourcePosition != null ? sourcePosition.hashCode() : 0);
        result = 31 * result + (destinationPosition != null ? destinationPosition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SubstitutionCheck{" +
                "checkerId='" + checkerId + '\'' +
                ", needed=" + needed +
                ", vehicleUpdate=" + vehicleUpdate +
                ", sourcePosition=" + sourcePosition +
                ", destinationPosition=" + destinationPosition +
                '}';
    }
}
