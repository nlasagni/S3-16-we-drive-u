package com.wedriveu.mobile.booking.presenter;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.booking.presenter.model.BookingPresentationModel;
import com.wedriveu.mobile.booking.view.BookingView;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.store.StoreFactoryImpl;
import com.wedriveu.mobile.store.VehicleStore;

/**
 * @author Nicola Lasagni on 29/07/2017.
 */
public class BookingPresenterImpl extends Fragment implements BookingPresenter {

    private static final int RENDER_DELAY = 200;

    private VehicleStore mVehicleStore;

    public static BookingPresenterImpl newInstance() {
        BookingPresenterImpl fragment = new BookingPresenterImpl();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mVehicleStore = StoreFactoryImpl.getInstance().createVehicleStore(getContext());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                renderContent();
            }
        }, RENDER_DELAY);
    }

    private void renderContent() {
        BookingView view = getBookingView();
        if (view != null) {
            Vehicle vehicle = mVehicleStore.getVehicle();
            view.renderView(createPresentationModel(vehicle));
        }
    }

    private BookingPresentationModel createPresentationModel(Vehicle vehicle) {
        BookingPresentationModel presentationModel = new BookingPresentationModel();
        presentationModel.setVehicleName(vehicle.getVehicleName());
        presentationModel.setLicensePlate(getString(R.string.vehicle_license_plate, vehicle.getLicencePlate()));
        presentationModel.setDescription(vehicle.getDescription());
        presentationModel.setImageUrl(vehicle.getPictureURL());
        presentationModel.setPickUpTime(getString(R.string.vehicle_pickup_time, vehicle.getArriveAtUserTime()));
        presentationModel.setArriveTime(getString(R.string.vehicle_arrives_at, vehicle.getArriveAtDestinationTime()));
        return presentationModel;
    }

    @Override
    public void onAcceptButtonClick() {
        //TODO Send booking request through dedicated service
    }

    @Override
    public void onDeclineButtonClick() {
        //TODO Go back to trip scheduling
    }

    private BookingView getBookingView() {
        BookingView view = null;
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            view = (BookingView) componentFinder.getView(BookingView.ID);
        }
        return view;
    }

}
