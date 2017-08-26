package com.wedriveu.shared.rabbitmq.message;

/**
 * class sent for responding to a vehicle entering request
 *
 * @author Michele Donati on 17/08/2017.
 */

public class EnterVehicleResponse {

    private String response;

    /**
     * @return the response for entering the vehicle
     */
    public String getResponse(){
        return response;
    }

    /**
     * @param newResponse the response for entering the vehicle
     */
    public void setResponse(String newResponse){
        this.response = newResponse;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnterVehicleResponse that = (EnterVehicleResponse) o;

        return response.equals(that.response);
    }

    @Override
    public int hashCode() {
        return response.hashCode();
    }

    @Override
    public String toString() {
        return "EnterVehicleResponse{" +
                "response='" + response + '\'' +
                '}';
    }
}
