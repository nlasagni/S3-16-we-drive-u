package com.wedriveu.mobile.booking.view;

import com.wedriveu.mobile.booking.viewmodel.model.TravellingMarkerPresentationModel;

/**
 * The base booking travelling view that shows a vehicle.
 *
 * @author Nicola Lasagni on 20/08/2017.
 */
public interface BookingTravellingView extends BookingView {

    /**
     * Shows the {@linkplain TravellingMarkerPresentationModel} passed in input.
     * @param presentationModel The model to be rendered.
     */
    void showTravellingMarker(TravellingMarkerPresentationModel presentationModel);

}
