package com.wedriveu.mobile.store;

import com.wedriveu.mobile.model.User;

/**
 *
 * UserStore stores user credentials after a successful login.
 *
 * @Author Marco Baldassarri, Nicola Lasagni
 * @Since 12/07/2017
 */
public interface UserStore {

    /**
     * Retrieves the stored User.
     *
     * @return User the user stored after login being processed
     */
    User getUser();

    /**
     * Stores a user. This method could be used to prevent the user from logging in every time he opens the App.
     * @param user
     */
    void storeUser(User user);

}
