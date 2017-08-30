package com.wedriveu.mobile.booking.viewmodel.model;

import android.support.annotation.DrawableRes;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Nicola Lasagni on 28/08/2017.
 */
public class TravellingMarkerPresentationModel {

    private int iconResource;
    private String title;
    private String subtitle;
    private LatLng position;

    public boolean hasIconResource() {
        return iconResource != 0;
    }

    public @DrawableRes int getIconResource() {
        return iconResource;
    }

    void setIconResource(@DrawableRes int iconResource) {
        this.iconResource = iconResource;
    }

    public String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

}
