package com.wedriveu.mobile.booking.viewmodel.model;

import com.google.android.gms.maps.model.LatLngBounds;

/**
 * The entity that contains all the data that can be displayed in a booking summary.
 *
 * @author Nicola Lasagni on 29/07/2017.
 */
public class BookingSummaryPresentationModel {

    private String vehicleName;
    private String licensePlate;
    private String imageUrl;
    private String description;
    private String pickUpTime;
    private String arriveTime;
    private TravellingMarkerPresentationModel userMarker;
    private TravellingMarkerPresentationModel destinationMarker;
    private TravellingMarkerPresentationModel headQuarterMarker;
    private LatLngBounds mapBounds;

    /**
     * Gets vehicle name.
     *
     * @return the vehicle name
     */
    public String getVehicleName() {
        return vehicleName;
    }

    /**
     * Sets vehicle name.
     *
     * @param vehicleName the vehicle name
     */
    void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    /**
     * Gets license plate.
     *
     * @return the license plate
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Sets license plate.
     *
     * @param licensePlate the license plate
     */
    void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    /**
     * Gets image url.
     *
     * @return the image url
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets image url.
     *
     * @param imageUrl the image url
     */
    void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets pick up time.
     *
     * @return the pick up time
     */
    public String getPickUpTime() {
        return pickUpTime;
    }

    /**
     * Sets pick up time.
     *
     * @param pickUpTime the pick up time
     */
    void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    /**
     * Gets arrive time.
     *
     * @return the arrive time
     */
    public String getArriveTime() {
        return arriveTime;
    }

    /**
     * Sets arrive time.
     *
     * @param arriveTime the arrive time
     */
    void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    /**
     * Gets user marker.
     *
     * @return the user marker
     */
    public TravellingMarkerPresentationModel getUserMarker() {
        return userMarker;
    }

    /**
     * Sets user marker.
     *
     * @param userMarker the user marker
     */
    void setUserMarker(TravellingMarkerPresentationModel userMarker) {
        this.userMarker = userMarker;
    }

    /**
     * Gets destination marker.
     *
     * @return the destination marker
     */
    public TravellingMarkerPresentationModel getDestinationMarker() {
        return destinationMarker;
    }

    /**
     * Sets destination marker.
     *
     * @param destinationMarker the destination marker
     */
    void setDestinationMarker(TravellingMarkerPresentationModel destinationMarker) {
        this.destinationMarker = destinationMarker;
    }

    /**
     * Gets head quarter marker.
     *
     * @return the head quarter marker
     */
    public TravellingMarkerPresentationModel getHeadQuarterMarker() {
        return headQuarterMarker;
    }

    /**
     * Sets head quarter marker.
     *
     * @param mHeadQuarterMarker the m head quarter marker
     */
    void setHeadQuarterMarker(TravellingMarkerPresentationModel mHeadQuarterMarker) {
        this.headQuarterMarker = mHeadQuarterMarker;
    }

    /**
     * Gets map bounds.
     *
     * @return the map bounds
     */
    public LatLngBounds getMapBounds() {
        return mapBounds;
    }

    /**
     * Sets map bounds.
     *
     * @param mapBounds the map bounds
     */
    void setMapBounds(LatLngBounds mapBounds) {
        this.mapBounds = mapBounds;
    }

}
