package com.wedriveu.mobile.entity.store;

import com.wedriveu.mobile.entity.model.User;

/**
 * Created by Marco on 12/07/2017.
 */
public class LoginStoreImpl implements LoginStore {

    private User user;

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void storeUser(User user) {
        this.user = user;
    }
}
