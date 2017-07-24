package com.wedriveu.mobile.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes a logged user
 *
 * @author Marco Baldassarri
 * @author Nicola Lasagni
 * @since 12/07/2017
 */
public class User {

    private String username;
    private String password;

    public User(@JsonProperty("username") String username,
                @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
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
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User otherUser = (User) obj;
        return username.equals(otherUser.username) && password.equals(otherUser.password);
    }

    @Override
    public String toString() {
        return "User: " + username + ", Password: " + password;
    }

}
