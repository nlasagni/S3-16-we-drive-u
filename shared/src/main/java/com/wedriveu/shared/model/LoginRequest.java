package com.wedriveu.shared.model;

/**
 * @author Nicola Lasagni on 04/08/2017.
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

}
