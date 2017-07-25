package com.wedriveu.services.authentication.control;

import com.wedriveu.services.authentication.entity.User;
import com.wedriveu.services.authentication.entity.UserStore;
import com.wedriveu.services.authentication.entity.UserStoreImpl;


/**
 * Created by ste on 12/07/2017.
 */
public class CredentialsCheckerImpl implements CredentialsChecker {

    private UserStore userStore;

    public CredentialsCheckerImpl() {
        userStore = new UserStoreImpl();
    }

    @Override
    public boolean confirmCredentials(String username, String password) {
        User user;
        userStore.mapEntityToJson();
        user = userStore.getUser(username);
        return user != null && user.getUsername().equals(username) && user.getPassword().equals(password);
    }

}
