package com.wedriveu.services.authentication.util;

import com.wedriveu.services.shared.model.User;

import java.util.Arrays;
import java.util.UUID;

/**
 * Utility class for managing user credentials.
 *
 * @author Nicola Lasagni on 02/09/2017.
 */
public class Credentials {

    private static final String DASH = "-";

    /**
     * Utility method to confirm user credentials.
     *
     * @param username the username to be confirmed
     * @param password the password to be confirmed
     * @return true if the credentials are correct, false otherwise
     */
    public static boolean confirmCredentials(String username, String password) {
        return Arrays.stream(User.USERS).anyMatch(user ->
                user.getUsername().equals(username) && user.getPassword().equals(password)
        );
    }

    /**
     * Utility method to generate a user id.
     *
     * @param username the username with which generate the id.
     * @return The user id
     */
    public static String generateUserId(String username) {
        UUID uuid = UUID.randomUUID();
        return username + DASH + uuid.toString();
    }

}
