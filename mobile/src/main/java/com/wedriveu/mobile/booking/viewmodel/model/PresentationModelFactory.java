package com.wedriveu.mobile.booking.viewmodel.model;

import com.wedriveu.mobile.model.Booking;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.shared.util.Position;

/**
 * @author Nicola Lasagni on 28/08/2017.
 */
public interface PresentationModelFactory {

    BookingSummaryPresentationModel createBookingSummaryPresentationModel(User user,
                                                                          Vehicle vehicle,
                                                                          Booking booking);

    TravellingMarkerPresentationModel createTravellingMarkerPresentationModel(Vehicle vehicle);

}
