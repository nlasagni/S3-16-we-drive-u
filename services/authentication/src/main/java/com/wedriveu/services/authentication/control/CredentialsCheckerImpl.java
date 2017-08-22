package com.wedriveu.services.authentication.control;

import com.wedriveu.services.authentication.entity.UserStore;
import com.wedriveu.services.authentication.entity.UserStoreImpl;
import com.wedriveu.services.shared.model.User;
import com.wedriveu.shared.util.Log;

import java.io.IOException;


/**
 * Created by ste on 12/07/2017.
 */
public class CredentialsCheckerImpl implements CredentialsChecker {

    private UserStore userStore;

    public CredentialsCheckerImpl() {
        initStore();
    }

    private void initStore() {
        try {
            userStore = new UserStoreImpl();
        } catch (IOException e) {
            Log.info("Error while creating UserStore");
        }
    }

    @Override
    public boolean confirmCredentials(String username, String password) {
        if (userStore == null) {
            return false;
        }
        User user;
        userStore.mapEntityToJson();
        user = userStore.getUser(username);
        return user != null && user.getUsername().equals(username) && user.getPassword().equals(password);
    }

}
