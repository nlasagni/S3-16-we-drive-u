package com.wedriveu.services.analytics.entity;

/**
 * @author Stefano Bernagozzi
 * This inteface models the <em>com.wedriveu.services.analytics.entity.AnalyticsVehicle' database store</em>.
 */


public interface AnalyticsStore {
    /**
     * get the vehicle by its license plate
     * @param licensePlate the string that represents the license plate of the <em>AnalyticsVehicle</em> that wants to be returned
     * @return a <em>AnalyticsVehicle</em> that contains the license plate and the status of the vehicle requested
     */
    public AnalyticsVehicle getVehicleByLicensePlate(String licensePlate);

    /**
     * adds a <em>AnalyticsVehicle</em> to StoreEntities
     * @param licensePlate the license plate of the <em>AnalyticsVehicle</em> that wants to be added
     * @param status the current status of the <em>AnalyticsVehicle</em> that wants to be added
     */
    public void addVehicle(String licensePlate, String status);

    /**
     * update the status of a <em>AnalyticsVehicle</em>
     * @param licensePlate the license plate of the <em>AnalyticsVehicle</em> that wants to be updated
     * @param status the current status of the <em>AnalyticsVehicle</em> that wants to be updated
     */
    public void updateVehicle(String licensePlate, String status);

    /**
     * clear the database
     */
    public void clear();

}


