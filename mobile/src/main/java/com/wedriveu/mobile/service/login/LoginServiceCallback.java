package com.wedriveu.mobile.service.login;

import com.wedriveu.mobile.model.User;

/**
 * <p>
 *     LoginServiceCallback
 * </p>
 * @Author Marco Baldassarri, Nicola Lasagni
 * @Since 12/07/2017
 */
public interface LoginServiceCallback {
    /**
     * Method called after the LoginService receives a synchronous response from the AuthenticationService backend.
     *
     * @param user the user object returned after the HTTP Rest interaction.
     *             If the user credential wasn't correct, this object is null.
     * @param errorMessage The error message returned from the login service.
     *
     */
    void onLoginFinished(User user, String errorMessage);
}
