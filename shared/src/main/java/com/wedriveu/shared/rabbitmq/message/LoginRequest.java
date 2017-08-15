package com.wedriveu.shared.rabbitmq.message;

/**
 * This class represents a login request performed by a client to be authenticated into the system.
 *
 * @author Nicola Lasagni on 04/08/2017.
 */
public class LoginRequest {

    private String requestId;
    private String username;
    private String password;

    /**
     * Gets request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets request id.
     *
     * @param requestId the request id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        LoginRequest otherLoginRequest = (LoginRequest) other;
        return checkRequestId(otherLoginRequest) &&
                checkUserName(otherLoginRequest) &&
                checkPassword(otherLoginRequest);
    }

    private boolean checkRequestId(LoginRequest otherLoginRequest) {
        return getRequestId() != null
                ? getRequestId().equals(otherLoginRequest.getRequestId())
                : otherLoginRequest.getRequestId() == null;
    }

    private boolean checkUserName(LoginRequest otherLoginRequest) {
        return getUsername() != null
                ? getUsername().equals(otherLoginRequest.getUsername())
                : otherLoginRequest.getUsername() == null;
    }

    private boolean checkPassword(LoginRequest otherLoginRequest) {
        return getPassword() != null
                ? getPassword().equals(otherLoginRequest.getPassword())
                : otherLoginRequest.getPassword() == null;
    }

    @Override
    public int hashCode() {
        int result = getRequestId() != null ? getRequestId().hashCode() : 0;
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "requestId='" + requestId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
