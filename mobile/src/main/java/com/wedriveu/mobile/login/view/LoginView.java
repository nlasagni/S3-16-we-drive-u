package com.wedriveu.mobile.login.view;

/**
 * Models the login View of the Android App, it should contain the Fragment with the Login UI components
 * @author Marco Baldassarri
 * @since 12/07/2017
 */
public interface LoginView {

    String TAG = LoginView.class.getSimpleName();

    /**
     * method called during the View creation. Sets the listeners for the UI components in the LoginView.
     */
    void renderView();

    /**
     * Method called in case the user credential inputs are wrong.
     * @param message The message to show to the user.
     */
    void renderError(String message);

}
