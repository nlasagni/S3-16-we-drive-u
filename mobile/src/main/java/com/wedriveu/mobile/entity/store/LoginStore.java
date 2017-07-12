package com.wedriveu.mobile.entity.store;

import com.wedriveu.mobile.entity.model.User;

/**
 *
 * LoginStore is a login repository where the user credentials are being stored after successful login.
 *
 * @Author Marco Baldassarri
 * @Since 12/07/2017
 */
public interface LoginStore {
    /**
     * Retrieves the saved User after the login is being performed.
     *
     * @return User the user stored after login being processed
     */
    User getUser();

    /**
     * Stores a user locally. This method could be used to prevent the user from logging in every time he opens the App.
     * @param user
     */
    void storeUser(User user);

}
