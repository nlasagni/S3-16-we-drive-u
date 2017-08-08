package com.wedriveu.shared.entity;

/**
 * @author Nicola Lasagni on 04/08/2017.
 *
 * This class represents a login request performed by a client to be authenticated into the system.
 */
public class LoginRequest {

    private String requestId;
    private String username;
    private String password;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        LoginRequest otherLoginRequest = (LoginRequest) other;
        return (getRequestId() != null ? getRequestId().equals(otherLoginRequest.getRequestId()) : otherLoginRequest.getRequestId() == null) &&
                (getUsername() != null ? getUsername().equals(otherLoginRequest.getUsername()) : otherLoginRequest.getUsername() == null) &&
                (getPassword() != null ? getPassword().equals(otherLoginRequest.getPassword()) : otherLoginRequest.getPassword() == null);
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
