package com.wedriveu.services.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Michele on 12/07/2017.
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
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

}

