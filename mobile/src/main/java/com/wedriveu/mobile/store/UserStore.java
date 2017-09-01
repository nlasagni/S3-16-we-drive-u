package com.wedriveu.mobile.store;

import com.wedriveu.mobile.model.User;

/**
 *
 * UserStore stores user credentials after a successful login.
 *
 * @author Marco Baldassarri
 * @author Nicola Lasagni
 */
public interface UserStore {

    /**
     * Retrieves the stored User.
     *
     * @return The user stored after login being processed
     */
    User getUser();

    /**
     * Stores a user.
     * This method should be used to prevent the user from logging in every time he opens the App.
     * @param user The logged user
     */
    void storeUser(User user);

}
