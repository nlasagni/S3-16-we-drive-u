package com.wedriveu.shared.entity;

/**
 * This class represents a login response of an authentication attempt into the system.
 *
 * @author Nicola Lasagni on 04/08/2017.
 */
public class LoginResponse {

    private boolean success;
    private String errorMessage;

    /**
     * Indicates if the operation was successful.
     *
     * @return the boolean
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets success.
     *
     * @param success the success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Gets error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets error message.
     *
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        LoginResponse otherLoginResponse = (LoginResponse) other;
        return success == otherLoginResponse.success &&
                (errorMessage != null
                        ? errorMessage.equals(otherLoginResponse.errorMessage)
                        : otherLoginResponse.errorMessage == null);
    }

    @Override
    public int hashCode() {
        int result = (isSuccess() ? 1 : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
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
