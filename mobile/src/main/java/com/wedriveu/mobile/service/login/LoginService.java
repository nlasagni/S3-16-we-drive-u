package com.wedriveu.mobile.service.login;

/**
 *
 * This service interacts with the AuthenticationService backend to perform the user login.
 *
 * @author  Marco Baldassarri
 * @author Nicola Lasagni
 * @since 12/07/2017
 */
public interface LoginService {

    /**
     * Login method calls the AuthenticationService and perform a login request.
     *
     * @param username username login credential
     * @param password password login credential
     * @param callback method called from the LoginService in order to give the proper result back to the ViewModel
     */
    void login(String username, String password, LoginServiceCallback callback);

}
