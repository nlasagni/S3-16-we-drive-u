package com.wedriveu.mobile.service.login;

import android.os.Handler;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.service.ServiceOperationHandler;

/**
 *
 * This service interacts with the AuthenticationService backend to perform the user login.
 *
 * @author  Marco Baldassarri
 * @author Nicola Lasagni
 */
public interface LoginService {

    /**
     * Login method calls the AuthenticationService and perform a login request.
     *
     * @param username username login credential
     * @param password password login credential
     * @param handler {@link Handler} called from the LoginService in order to give the proper result
     *                               back to the caller
     */
    <T> void login(String username, String password, ServiceOperationHandler<T, User> handler);

}
