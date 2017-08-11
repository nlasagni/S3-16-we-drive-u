package com.wedriveu.shared.entity;

/**
 * Created by Michele on 10/08/2017.
 */

public class RegisterToServiceResponse {

    private boolean registerOk;

    public boolean getRegisterOk() {
        return registerOk;
    }

    public void setRegisterOk(boolean registerOk) {
        this.registerOk = registerOk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisterToServiceResponse that = (RegisterToServiceResponse) o;

        return registerOk == that.registerOk;
    }

    @Override
    public int hashCode() {
        return (registerOk ? 1 : 0);
    }

    @Override
    public String toString() {
        return "RegisterToServiceResponse{" +
                "registerOk=" + registerOk +
                '}';
    }
}
