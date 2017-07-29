package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.booking.presenter.BookingPresenter;
import com.wedriveu.mobile.booking.presenter.model.BookingPresentationModel;
import com.wedriveu.mobile.util.Constants;

/**
 * Created by nicolalasagni on 29/07/2017.
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

    public static BookingViewImpl newInstance(String presenterId) {
        BookingViewImpl fragment = new BookingViewImpl();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.VIEW_MODEL_ID, presenterId);
        fragment.setArguments(bundle);
        return fragment;
    }

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
        BookingPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onAcceptButtonClick();
        }
    }

    private void declineBooking() {
        BookingPresenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onDeclineButtonClick();
        }
    }

    @Override
    public void renderView(BookingPresentationModel presentationModel) {
        //TODO Render BookingPresentationModel data
    }

    private BookingPresenter getPresenter() {
        BookingPresenter presenter = null;
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            String presenterId = getArguments().getString(Constants.VIEW_MODEL_ID);
            presenter = (BookingPresenter) componentFinder.getViewModel(presenterId);
        }
        return presenter;
    }

}
