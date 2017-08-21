package com.wedriveu.services.vehicle.rabbitmq;

import com.wedriveu.services.shared.model.Vehicle;
import com.wedriveu.shared.util.Position;

import java.util.List;

/**
 * Created by Michele on 19/08/2017.
 */

public class SubstitutionRequest {

    private Position position;
    private List<Vehicle> vehicleList;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position newPosition) {
        this.position = newPosition;
    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubstitutionRequest that = (SubstitutionRequest) o;

        return position.equals(that.position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }

    @Override
    public String toString() {
        return "SubstitutionRequest{" +
                "position=" + position +
                '}';
    }

}
