package com.wedriveu.services.shared.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User model class.
 *
 * @author Michele on 12/07/2017.
 */
public class User {

    private static final String PASSWORD = "password";
    public static final User[] USERS = {
            new User("Michele", PASSWORD),
            new User("Stefano", PASSWORD),
            new User("Nicola", PASSWORD),
            new User("Marco", PASSWORD),
    };

    private String username;
    private String password;

    /**
     * Instantiates a new User.
     *
     * @param username the username
     * @param password the password
     */
    public User(@JsonProperty("username") String username,
                @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return (username != null ? username.equals(user.username) : user.username == null) &&
                (password != null ? password.equals(user.password) : user.password == null);
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

