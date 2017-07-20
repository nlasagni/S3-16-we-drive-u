package com.wedriveu.mobile.login.viewmodel;

/**
 *
 * Represents the View business logic. Performs the logic tasks connected with the UI elements.
 * @author Marco Baldassarri
 * @since 12/07/2017
 */
public interface LoginViewModel {

    String TAG = LoginViewModel.class.getSimpleName();

    /**
     * Method called when the user clicks to the Login button.
     * @param username the username written by the user in the Login form
     * @param password the password written by the user in the Login form
     */
    void onLoginButtonClick(String username, String password);

}
