package com.wedriveu.shared.rabbitmq.message;

/**
 * @author Michele Donati on 10/08/2017.
 *
 * This class represents a "register" request that the vehicles sends to the service.
 */
public class RegisterToServiceRequest {

    private String license;

    /**
     * @return the license of the vehicle that wants to be added
     */
    public String getLicense() {
        return license;
    }

    /**
     * @param license the license of the vehicle that wants to be added
     */
    public void setLicense(String license) {
        this.license = license;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisterToServiceRequest that = (RegisterToServiceRequest) o;

        return license.equals(that.license);
    }

    @Override
    public int hashCode() {
        return license.hashCode();
    }

    @Override
    public String toString() {
        return "RegisterToServiceRequest{" +
                "license='" + license + '\'' +
                '}';
    }

}
