package com.wedriveu.shared.entity;

/**
 * @author Nicola Lasagni on 04/08/2017.
 */
public class LoginResponse {

    private boolean success;
    private String errorMessage;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        LoginResponse otherLoginResponse = (LoginResponse) other;
        return getSuccess() == otherLoginResponse.getSuccess() &&
                (getErrorMessage() != null
                        ? getErrorMessage().equals(otherLoginResponse.getErrorMessage())
                        : otherLoginResponse.getErrorMessage() == null);
    }

    @Override
    public int hashCode() {
        int result = (getSuccess() ? 1 : 0);
        result = 31 * result + (getErrorMessage() != null ? getErrorMessage().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
