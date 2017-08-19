package com.wedriveu.mobile.booking.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.booking.viewmodel.BookingViewModel;
import com.wedriveu.mobile.booking.viewmodel.model.BookingPresentationModel;

/**
 * @author Nicola Lasagni on 29/07/2017.
 */
public class BookingViewImpl extends Fragment implements BookingView {

    private View mView;
    private TextView mVehicleName;
    private TextView mVehicleLicensePlate;
    private ImageView mVehicleImage;
    private TextView mVehicleDescription;
    private TextView mVehiclePickUpTime;
    private TextView mVehicleArriveTime;
    private Button mAcceptButton;
    private Button mDeclineButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_booking, container, false);
        setupViewComponents();
        setupListeners();
        return mView;
    }

    private void setupViewComponents() {
        mVehicleName = (TextView) mView.findViewById(R.id.name);
        mVehicleLicensePlate = (TextView) mView.findViewById(R.id.license_plate);
        mVehicleImage = (ImageView) mView.findViewById(R.id.image);
        mVehicleDescription = (TextView) mView.findViewById(R.id.description);
        mVehiclePickUpTime = (TextView) mView.findViewById(R.id.pick_up_time);
        mVehicleArriveTime = (TextView) mView.findViewById(R.id.arrive_time);
        mAcceptButton = (Button) mView.findViewById(R.id.accept_button);
        mDeclineButton = (Button) mView.findViewById(R.id.decline_button);
    }

    private void setupListeners() {
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptBooking();
            }
        });
        mDeclineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declineBooking();
            }
        });
    }

    private void acceptBooking() {
        BookingViewModel presenter = getViewModel();
        if (presenter != null) {
            presenter.onAcceptButtonClick();
        }
    }

    private void declineBooking() {
        BookingViewModel presenter = getViewModel();
        if (presenter != null) {
            presenter.onDeclineButtonClick();
        }
    }

    @Override
    public void renderView(BookingPresentationModel presentationModel) {
        setActionBarTitle();
        if (presentationModel != null) {
            mVehicleName.setText(presentationModel.getVehicleName());
            mVehicleLicensePlate.setText(presentationModel.getLicensePlate());
            mVehicleDescription.setText(presentationModel.getDescription());
            mVehiclePickUpTime.setText(presentationModel.getPickUpTime());
            mVehicleArriveTime.setText(presentationModel.getArriveTime());
            Glide.with(this).load(presentationModel.getImageUrl()).into(mVehicleImage);
        }
    }

    @Override
    public void renderError(String errorMessage) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.common_warning)
                .setMessage(errorMessage)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void setActionBarTitle() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.vehicle_title);
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
