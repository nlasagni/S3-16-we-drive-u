package com.wedriveu.mobile.app;

import android.support.v4.app.Fragment;

/**
 *
 * Represents an entity capable of retrieving view and viewmodels android components.
 *
 * @author Marco Baldassarri
 * @author Nicola Lasagni
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
