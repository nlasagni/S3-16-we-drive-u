package com.wedriveu.mobile.app;

/**
 *
 *Dialog provider interface
 * 
 * @author Nicola Lasagni
 * @since 18/07/2017
 */
public interface DialogProvider {

    /**
     * Shows a dialog over all other views.
     *
     * @param message The message to be shown.
     */
    void showPopOverDialog(String message);

    /**
     * Shows a full screen progress dialog.
     */
    void showProgressDialog();

    /**
     * Dismisses the progress dialog.
     */
    void dismissProgressDialog();

}
