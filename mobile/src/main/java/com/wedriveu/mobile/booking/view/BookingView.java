package com.wedriveu.mobile.booking.view;

import com.wedriveu.mobile.booking.presenter.model.BookingPresentationModel;

/**
 * @author Nicola Lasagni on 28/07/2017.
 */
public interface BookingView {

    String ID = BookingView.class.getSimpleName();

    void renderView(BookingPresentationModel presentationModel);

}
