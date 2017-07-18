package com.wedriveu.mobile.app;

import android.app.Fragment;

/**
 *
 * <p>
 *     Component finder interface
 * </p>
 * @author Marco Baldassarri, Nicola Lasagni
 * @since 12/07/2017
 */
public interface ComponentFinder {

    /**
     *
     * This method returns the specific view given a tag.
     *
     * @param tag The tag associated with the specific view
     * @return Fragment the requested Android Fragment
     */
    Fragment getView(String tag);

    /**
     *
     * This method returns the specific view model given a tag.
     *
     * @param tag The tag associated with the specific view
     * @return Fragment the requested Android Fragment
     */
    Fragment getViewModel(String tag);

}