package com.wedriveu.services.authentication.entity;

/**
 * Created by Michele on 12/07/2017.
 */

import com.wedriveu.services.shared.model.User;

/**
 * The interface User store. This inteface models the <em>Users' database domain</em>.
 *
 * @author Michele Donati
 * @author Nicola Lasagni
 */
public interface UserStore {

    /**
     * Adds a User into the store.
     *
     * @param user the user to be added
     * @return true if this operation was successful, false otherwise
     */
    boolean addUser(User user);

    /**
     * Clears the store
     */
    void clear();

}
