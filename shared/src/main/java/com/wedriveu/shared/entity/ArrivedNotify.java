package com.wedriveu.shared.entity;

/**
 * @author Michele Donati on 11/08/2017.
 */

public class ArrivedNotify {

    private String license;

    public String getLicense() {
        return license;
    }

    public void setLicense(String newLicense) {
        this.license = newLicense;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArrivedNotify that = (ArrivedNotify) o;

        return license.equals(that.license);
    }

    @Override
    public int hashCode() {
        return license.hashCode();
    }

    @Override
    public String toString() {
        return "ArrivedNotify{" +
                "license='" + license + '\'' +
                '}';
    }

}
