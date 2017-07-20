package com.wedriveu.mobile.login.router;

import com.wedriveu.mobile.app.DialogProvider;

/**
 * <p>
 *      LoginRouter is the interface used by the LoginViewModel logic to switch to the next Fragment transaction.
 * <p/>
 * <p>
 *     Eg. After the user clicks on Login button, the login process is terminated and the App performs
 *     a fragment transaction to the Scheduling application component.
 * </p>
 *
 * @author Marco Baldassarri
 * @since 12/07/2017
 */
public interface LoginRouter extends DialogProvider {

    /**
     * Method used by the MainActivity to show the Scheduling Fragment
     * in order to let the user choosing a destination address.
     */
    void showTripScheduling();

}
