package com.wedriveu.mobile.login.router;

import com.wedriveu.mobile.app.DialogProvider;

/**
 * 
 *  LoginRouter is the interface used by the LoginViewModel logic to switch to the next Fragment transaction.
 *
 *  Eg. After the user clicks on Login button, the login process is terminated and the App performs
 *  a fragment transaction to the Scheduling application component.
 *
 * @author Marco Baldassarri
 */
public interface LoginRouter extends DialogProvider {

    /**
     * Method used by the MainActivity to show the Scheduling Fragment
     * in order to let the user choosing a destination address.
     */
    void showTripScheduling();

}
