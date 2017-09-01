package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.booking.viewmodel.model.BookingSummaryPresentationModel;

/**
 * The view that shows the substitution vehicle travelling to the user.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public class BookingUserSubstitutionTravellingView extends AbstractBookingTravellingView {

    /**
     * The id of this view.
     */
    public static final String ID = BookingUserSubstitutionTravellingView.class.getSimpleName();

    private TextView mPickUpTime;
    private TextView mArriveTime;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_booking_travelling_substitution;
    }

    @Override
    protected void setupViewComponents(View view, Bundle savedInstanceState) {
        super.setupViewComponents(view, savedInstanceState);
        mPickUpTime = (TextView) view.findViewById(R.id.pick_up_time);
        mArriveTime = (TextView) view.findViewById(R.id.arrive_time);
    }

    @Override
    public void renderView(BookingSummaryPresentationModel presentationModel) {
        super.renderView(presentationModel);
        mPickUpTime.setText(presentationModel.getPickUpTime());
        mArriveTime.setText(presentationModel.getArriveTime());
    }
}
