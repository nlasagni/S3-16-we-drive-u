package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.booking.viewmodel.BookingViewModel;
import com.wedriveu.mobile.booking.viewmodel.model.BookingSummaryPresentationModel;

/**
 * The first booking section view, shows the summary of a booking.
 *
 * @author Nicola Lasagni on 29/07/2017.
 */
public class BookingSummaryView extends AbstractBookingView {

    /**
     * The id of this view.
     */
    public static final String ID = BookingSummaryView.class.getSimpleName();

    private TextView mVehicleName;
    private TextView mVehicleLicensePlate;
    private ImageView mVehicleImage;
    private TextView mVehicleDescription;
    private TextView mVehiclePickUpTime;
    private TextView mVehicleArriveTime;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_booking_summary;
    }

    @Override
    protected void setupViewComponents(View view, Bundle savedInstanceState) {
        mVehicleName = (TextView) view.findViewById(R.id.name);
        mVehicleLicensePlate = (TextView) view.findViewById(R.id.license_plate);
        mVehicleImage = (ImageView) view.findViewById(R.id.image);
        mVehicleDescription = (TextView) view.findViewById(R.id.description);
        mVehiclePickUpTime = (TextView) view.findViewById(R.id.pick_up_time);
        mVehicleArriveTime = (TextView) view.findViewById(R.id.arrive_time);
        Button acceptButton = (Button) view.findViewById(R.id.accept_button);
        Button declineButton = (Button) view.findViewById(R.id.decline_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptBooking();
            }
        });
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineBooking();
            }
        });
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.vehicle_title;
    }

    @Override
    protected void renderPresentationModel(BookingSummaryPresentationModel presentationModel) {
        if (presentationModel != null) {
            mVehicleName.setText(presentationModel.getVehicleName());
            mVehicleLicensePlate.setText(presentationModel.getLicensePlate());
            mVehicleDescription.setText(presentationModel.getDescription());
            mVehiclePickUpTime.setText(presentationModel.getPickUpTime());
            mVehicleArriveTime.setText(presentationModel.getArriveTime());
            Glide.with(this).load(presentationModel.getImageUrl()).into(mVehicleImage);
        }
    }

    private void acceptBooking() {
        BookingViewModel presenter = getViewModel();
        if (presenter != null) {
            presenter.onAcceptBookingButtonClick();
        }
    }

    private void declineBooking() {
        BookingViewModel presenter = getViewModel();
        if (presenter != null) {
            presenter.onDeclineBookingButtonClick();
        }
    }

    private BookingViewModel getViewModel() {
        BookingViewModel presenter = null;
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            presenter = (BookingViewModel) componentFinder.getViewModel(BookingViewModel.ID);
        }
        return presenter;
    }

}
