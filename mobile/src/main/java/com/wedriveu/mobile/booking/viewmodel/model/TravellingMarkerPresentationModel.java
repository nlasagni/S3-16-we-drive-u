package com.wedriveu.mobile.booking.viewmodel.model;

import android.support.annotation.DrawableRes;
import com.google.android.gms.maps.model.LatLng;

/**
 * The entity that contains all the data that can be displayed in a travelling view.
 *
 * @author Nicola Lasagni on 28/08/2017.
 */
public class TravellingMarkerPresentationModel {

    private int iconResource;
    private String title;
    private String subtitle;
    private LatLng position;

    /**
     * Has icon resource boolean.
     *
     * @return true if this model has an icon resource
     */
    public boolean hasIconResource() {
        return iconResource != 0;
    }

    /**
     * Gets icon resource.
     *
     * @return the icon resource
     */
    public @DrawableRes int getIconResource() {
        return iconResource;
    }

    /**
     * Sets icon resource.
     *
     * @param iconResource the icon resource
     */
    void setIconResource(@DrawableRes int iconResource) {
        this.iconResource = iconResource;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets subtitle.
     *
     * @return the subtitle
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Sets subtitle.
     *
     * @param subtitle the subtitle
     */
    void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public LatLng getPosition() {
        return position;
    }

    /**
     * Sets position.
     *
     * @param position the position
     */
    void setPosition(LatLng position) {
        this.position = position;
    }

}
