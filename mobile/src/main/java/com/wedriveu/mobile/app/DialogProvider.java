package com.wedriveu.mobile.app;

/**
 *
 *Dialog provider interface
 * 
 * @author Nicola Lasagni
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
