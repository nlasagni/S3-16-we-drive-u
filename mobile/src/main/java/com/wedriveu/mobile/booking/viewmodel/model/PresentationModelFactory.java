package com.wedriveu.mobile.booking.viewmodel.model;

import com.wedriveu.mobile.model.Booking;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.model.Vehicle;

/**
 * A factory of {@linkplain BookingSummaryPresentationModel} and {@linkplain TravellingMarkerPresentationModel}.
 *
 * @author Nicola Lasagni on 28/08/2017.
 */
public interface PresentationModelFactory {

    /**
     * Create booking summary presentation model.
     *
     * @param user    the user used to create the presentation model
     * @param vehicle the vehicle used to create the presentation model
     * @param booking the booking used to create the presentation model
     * @return the booking summary presentation model
     */
    BookingSummaryPresentationModel createBookingSummaryPresentationModel(User user,
                                                                          Vehicle vehicle,
                                                                          Booking booking);

    /**
     * Create travelling marker presentation model.
     *
     * @param vehicle the vehicle used to create the presentation model
     * @return the travelling marker presentation model
     */
    TravellingMarkerPresentationModel createTravellingMarkerPresentationModel(Vehicle vehicle);

}
