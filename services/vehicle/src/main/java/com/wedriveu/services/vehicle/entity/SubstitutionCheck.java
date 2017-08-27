package com.wedriveu.services.vehicle.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wedriveu.shared.rabbitmq.message.UpdateToService;
import com.wedriveu.shared.util.Position;

/**
 * Represents a substitution check used when a vehicle notify this service with a "broken" status.
 *
 * @author Nicola Lasagni on 25/08/2017.
 */
public class SubstitutionCheck {

    private String checkerId;
    private boolean needed;
    private UpdateToService updateToService;
    private Position sourcePosition;
    private Position destinationPosition;

    public SubstitutionCheck(@JsonProperty("checkerId") String checkerId,
                             @JsonProperty("needed") boolean needed,
                             @JsonProperty("updateToService") UpdateToService updateToService,
                             @JsonProperty("sourcePosition") Position sourcePosition,
                             @JsonProperty("destinationPosition") Position destinationPosition) {
        this.checkerId = checkerId;
        this.needed = needed;
        this.updateToService = updateToService;
        this.sourcePosition = sourcePosition;
        this.destinationPosition = destinationPosition;
    }

    public String getCheckerId() {
        return checkerId;
    }

    public boolean isNeeded() {
        return needed;
    }

    public UpdateToService getUpdateToService() {
        return updateToService;
    }

    public Position getSourcePosition() {
        return sourcePosition;
    }

    public Position getDestinationPosition() {
        return destinationPosition;
    }
}
