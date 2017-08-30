package com.wedriveu.mobile.booking.viewmodel.model;

import com.google.android.gms.maps.model.LatLngBounds;

/**
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
    private LatLngBounds mapBounds;

    public String getVehicleName() {
        return vehicleName;
    }

    void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getPickUpTime() {
        return pickUpTime;
    }

    void setPickUpTime(String pickUpTime) {
        this.pickUpTime = pickUpTime;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public TravellingMarkerPresentationModel getUserMarker() {
        return userMarker;
    }

    void setUserMarker(TravellingMarkerPresentationModel userMarker) {
        this.userMarker = userMarker;
    }

    public TravellingMarkerPresentationModel getDestinationMarker() {
        return destinationMarker;
    }

    void setDestinationMarker(TravellingMarkerPresentationModel destinationMarker) {
        this.destinationMarker = destinationMarker;
    }

    public LatLngBounds getMapBounds() {
        return mapBounds;
    }

    void setMapBounds(LatLngBounds mapBounds) {
        this.mapBounds = mapBounds;
    }
}
