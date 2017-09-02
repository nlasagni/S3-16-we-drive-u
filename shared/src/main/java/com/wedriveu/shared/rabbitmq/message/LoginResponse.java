package com.wedriveu.shared.rabbitmq.message;

/**
 * This class represents a login response of an authentication attempt into the system.
 *
 * @author Nicola Lasagni on 04/08/2017.
 */
public class LoginResponse {

    private boolean success;
    private String userId;
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
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoginResponse)) {
            return false;
        }
        LoginResponse that = (LoginResponse) o;
        return success == that.success &&
                (userId != null ? userId.equals(that.userId) : that.userId == null) &&
                (errorMessage != null ? errorMessage.equals(that.errorMessage) : that.errorMessage == null);
    }

    @Override
    public int hashCode() {
        int result = (success ? 1 : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (errorMessage != null ? errorMessage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", userId='" + userId + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
