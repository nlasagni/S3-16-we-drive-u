package com.wedriveu.shared.rabbitmq.message;

/**
 * @author Michele Donati on 17/08/2017.
 */

public class EnterVehicleResponse {

    private String response;

    public String getResponse(){
        return response;
    }

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
