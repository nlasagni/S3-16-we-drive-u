package com.wedriveu.services.authentication.control;

/**
 * Created by ste on 12/07/2017.
 */
/**
 * @author Stefano Bernagozzi
 * This inteface models the authentication logic.
 */
public interface CredentialsChecker {
    /**
     * Returns the success or failure of the authentication process given the user's username and password in input.
     * @param username Identifies the username associated to the <em>user</em>.
     * @param password Identifies the password associated to the <em>user</em>.
     * @return <strong>True</strong> if the username and the password are associated to a <em>User</em> class into the
     * <em>userStore</em>, <strong>false</strong> otherwise.
     */
    boolean confirmCredentials(String username, String password);

}
