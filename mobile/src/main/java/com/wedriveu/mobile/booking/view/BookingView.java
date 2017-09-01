package com.wedriveu.mobile.booking.view;

import com.wedriveu.mobile.booking.viewmodel.model.BookingSummaryPresentationModel;

/**
 * The base view of the booking summary.
 *
 * @author Nicola Lasagni on 28/07/2017.
 */
public interface BookingView {

    /**
     * Renders the content passed in input.
     * @param presentationModel The model to be rendered.
     */
    void renderView(BookingSummaryPresentationModel presentationModel);

}
