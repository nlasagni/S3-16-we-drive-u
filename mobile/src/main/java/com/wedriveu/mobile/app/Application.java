package com.wedriveu.mobile.app;

import android.app.Fragment;

/**
 *
 * <p>
 *     Application Main Activity interface
 * </p>
 * @Author Marco Baldassarri
 * @Since 12/07/2017
 */
public interface Application {

    /**
     *
     * This method returns the specific Fragment given a tag.
     *
     * @param tag The tag associated with the specific fragment
     * @return Fragment the requested Android Fragment
     */
    Fragment getView(String tag);

}
