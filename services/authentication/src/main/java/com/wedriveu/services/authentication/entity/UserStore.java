package com.wedriveu.services.authentication.entity;

/**
 * Created by Michele on 12/07/2017.
 */

import com.wedriveu.services.shared.model.User;

/**
 * @author Michele Donati
 * This inteface models the <em>Users' database domain</em>.
 */
public interface UserStore {

    /**
     * Maps an <em>User</em> object in a JSon object.
     */
    void mapEntityToJson();

    /**
     * @param username Identifies the <em>User</em>'s <em>username</em> that must be authenticated.
     * @return Returns the <em>User</em> authenticated, only if the authentication is succesfull.
     */
    User getUser(String username);

}
