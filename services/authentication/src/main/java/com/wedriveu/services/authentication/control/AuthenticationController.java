package com.wedriveu.services.authentication.control;

import com.wedriveu.shared.rabbitmq.message.LoginRequest;
import com.wedriveu.shared.rabbitmq.message.LoginResponse;

/**
 * The Authentication Service Controller that manages authentication operations.
 *
 * @author Nicola Lasagni on 02/09/2017.
 */
public interface AuthenticationController {

    /**
     * Logs a user into the system.
     *
     * @param request the login request that contains all the data needed to login
     * @return the {@link LoginResponse} containing the login operation result
     */
    LoginResponse login(LoginRequest request);

}
