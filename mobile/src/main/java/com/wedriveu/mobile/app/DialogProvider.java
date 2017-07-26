package com.wedriveu.mobile.app;

/**
 *
 * <p>
 *     Dialog provider interface
 * </p>
 * @author Nicola Lasagni
 * @since 18/07/2017
 */
public interface DialogProvider {

    /**
     * Shows a full screen progress dialog.
     */
    void showProgressDialog();

    /**
     * Dismisses the progress dialog.
     */
    void dismissProgressDialog();

}
