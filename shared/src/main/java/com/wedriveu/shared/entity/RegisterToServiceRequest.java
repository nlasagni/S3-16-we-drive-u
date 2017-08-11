package com.wedriveu.shared.entity;

/**
 * Created by Michele on 10/08/2017.
 */
public class RegisterToServiceRequest {

    private String license;

    public String getLicense() {
        return license;
    }

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
