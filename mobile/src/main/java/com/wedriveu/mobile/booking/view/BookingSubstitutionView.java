package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.view.View;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.booking.viewmodel.model.BookingSummaryPresentationModel;

/**
 * A simple view that shows the waiting substitution informations.
 *
 * @author Nicola Lasagni on 28/08/2017.
 */
public class BookingSubstitutionView extends AbstractBookingView {

    /**
     * The id of this view.
     */
    public static final String ID = BookingSubstitutionView.class.getSimpleName();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_booking_substitution;
    }

    @Override
    protected void setupViewComponents(View view, Bundle savedInstanceState) {
        //Empty
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.app_name;
    }

    @Override
    protected void renderPresentationModel(BookingSummaryPresentationModel presentationModel) {
        //Empty
    }

}
