package com.wedriveu.services.authentication.util;

/**
 * The global Constants of the Authentication Service.
 *
 * @author Nicola Lasagni on 08/08/2017.
 */
public interface Constants extends com.wedriveu.shared.util.Constants {

    /**
     * Message used when user credentials are missing.
     */
    String USERNAME_PASSWORD_MISSING = "Username or Password missing";
    /**
     * Message used when user credentials are not correct.
     */
    String USERNAME_PASSWORD_WRONG = "Wrong Username or Password";

    /**
     * The {@link io.vertx.core.eventbus.EventBus} addresses used by the service verticles to communicate.
     */
    interface EventBus {
        /**
         * Address use to send a start login message.
         */
        String START_LOGIN = "service.authentication.login";
        /**
         * Address use to send a login completed message.
         */
        String LOGIN_COMPLETED = "service.authentication.login.completed";
    }

    /**
     * The constants needed to pud specific data into {@link io.vertx.core.eventbus.EventBus} messages.
     */
    interface Message {
        /**
         * The request id key.
         */
        String REQUEST_ID = "requestId";
        /**
         * The response key.
         */
        String RESPONSE = "response";
    }

}
