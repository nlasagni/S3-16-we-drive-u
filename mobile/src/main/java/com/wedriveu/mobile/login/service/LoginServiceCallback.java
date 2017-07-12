package com.wedriveu.mobile.login.service;

import com.wedriveu.mobile.entity.model.User;

/**
 * <p>
 *     LoginServiceCallback
 * </p>
 * @Author Marco Baldassarri
 * @Since 12/07/2017
 */
public interface LoginServiceCallback {
    /**
     * Method called after the LoginService receives a synchronous response from the AuthenticationService backend.
     *
     * @param user the user object returned after the HTTP Rest interaction. If the user credential wasn't correct, this object is null.
     */
    void onLoginFinished(User user);
}
