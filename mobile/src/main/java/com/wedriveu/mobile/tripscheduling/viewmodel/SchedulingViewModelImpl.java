package com.wedriveu.mobile.tripscheduling.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.app.FactoryProvider;
import com.wedriveu.mobile.model.Booking;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceFactory;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.store.BookingStore;
import com.wedriveu.mobile.store.StoreFactory;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.mobile.store.VehicleStore;
import com.wedriveu.mobile.tripscheduling.router.SchedulingRouter;
import com.wedriveu.mobile.tripscheduling.view.SchedulingView;
import com.wedriveu.mobile.util.Constants;
import com.wedriveu.mobile.util.location.LocationService;
import com.wedriveu.mobile.util.location.LocationServiceImpl;
import com.wedriveu.mobile.util.location.LocationServiceListener;
import com.wedriveu.shared.util.Position;

/**
 * The {@linkplain SchedulingViewModel} implementation.
 *
 * @author Marco on 18/07/2017.
 * @author Nicola Lasagni on 29/07/2017
 */
public class SchedulingViewModelImpl extends Fragment implements SchedulingViewModel, LocationServiceListener {

    private SchedulingServiceHandler<Vehicle> mFindNearestVehictledHandler;
    private SchedulingRouter mRouter;
    private LocationService mLocationService;
    private SchedulingService mSchedulingService;
    private UserStore mUserStore;
    private BookingStore mBookingStore;
    private VehicleStore mVehicleStore;
    private Place mPickUpPlace;
    private Place mDestinationPlace;
    private Position mUserPosition;
    private boolean mForDestination;

    /**
     * New instance of a {@linkplain SchedulingViewModel}.
     *
     * @return the booking view model
     */
    public static SchedulingViewModelImpl newInstance() {
        SchedulingViewModelImpl fragment = new SchedulingViewModelImpl();
        fragment.setRetainInstance(true);
        return fragment;
    }

    // This deprecated method has been chosen because the minTargetVersion is 19.
    // Switch to minTargetVersion >= 23 in order to use the new onAttach method.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRouter = (SchedulingRouter) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        mFindNearestVehictledHandler = new FindNearestVehicleHandler(this);
        StoreFactory storeFactory = ((FactoryProvider) getActivity()).provideStoreFactory();
        ServiceFactory serviceFactory = ((FactoryProvider) getActivity()).provideServiceFactory();
        mUserStore = storeFactory.createUserStore();
        mBookingStore = storeFactory.createBookingStore();
        mVehicleStore = storeFactory.createVehicleStore();
        mSchedulingService = serviceFactory.createSchedulingService();
        mLocationService = LocationServiceImpl.getInstance(activity);
    }

    @Override
    public void onLocationAvailable(Location location) {
        mUserPosition = new Position(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onLocationServiceDisabled() {
        mUserPosition = null;
    }

    @Override
    public void onSearchVehicleButtonClick(boolean useUserPosition) {
        mRouter.showProgressDialog();
        LatLng destination = mDestinationPlace.getLatLng();
        String address = mDestinationPlace.getAddress().toString();
        Booking booking =
                new Booking(null,
                        address,
                        null,
                        new Position(destination.latitude, destination.longitude));
        mBookingStore.storeBooking(booking);
        String username = mUserStore.getUser().getUsername();
        if (useUserPosition) {
            Location location = mLocationService.getLastKnownLocation();
            mUserPosition = new Position(location.getLatitude(), location.getLongitude());
        } else {
            LatLng pickUp = mPickUpPlace.getLatLng();
            mUserPosition = new Position(pickUp.latitude, pickUp.longitude);
        }
        if (mUserPosition.getLatitude() != 0 && mUserPosition.getLongitude() != 0 && mDestinationPlace != null) {
            mFindNearestVehictledHandler.refreshReference(this);
            mSchedulingService.findNearestVehicle(username, mUserPosition, mDestinationPlace, mFindNearestVehictledHandler);
        } else {
            SchedulingView schedulingView = (SchedulingView) getComponentFinder().getView(SchedulingView.ID);
            schedulingView.renderError(getString(R.string.scheduling_position_not_found));
        }
    }

    @Override
    public void startPlaceAutocomplete(boolean forDestination) {
        try {
            mForDestination = forDestination;
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            getActivity().startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(SchedulingViewModel.ID, "GooglePlayServices not installed", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SchedulingView schedulingView = (SchedulingView) getComponentFinder().getView(SchedulingView.ID);
        if (resultCode == Activity.RESULT_OK) {
            Place place = PlaceAutocomplete.getPlace(getActivity(), data);
            if (mForDestination) {
                mDestinationPlace = place;
                schedulingView.showDestinationAddress(mDestinationPlace);
            } else {
                mPickUpPlace = place;
                schedulingView.showPickUpAddress(mPickUpPlace);
            }
        }
    }

    private void onFindNearestVehicleFinished(Vehicle vehicle, String errorMessage) {
        mRouter.dismissProgressDialog();
        if (!TextUtils.isEmpty(errorMessage)) {
            mVehicleStore.clear();
            mBookingStore.clear();
            SchedulingView schedulingView = (SchedulingView) getComponentFinder().getView(SchedulingView.ID);
            schedulingView.renderError(errorMessage);
        } else {
            Booking storedBooking = mBookingStore.getBooking();
            Booking booking =
                    new Booking(vehicle.getLicencePlate(),
                            storedBooking.getDestinationAddress(),
                            mUserPosition,
                            storedBooking.getDestinationPosition());
            mBookingStore.storeBooking(booking);
            mVehicleStore.storeVehicle(vehicle);
            mRouter.showBookingSummary();
        }
    }

    private ComponentFinder getComponentFinder() {
        return ((ComponentFinder) getActivity());
    }

    private static class FindNearestVehicleHandler extends SchedulingServiceHandler<Vehicle> {

        private FindNearestVehicleHandler(SchedulingViewModelImpl viewModel) {
            super(viewModel);
        }

        @Override
        protected void handleMessage(SchedulingViewModelImpl viewModel,
                                     ServiceResult<Vehicle> result) {
            viewModel.onFindNearestVehicleFinished(result.getResult(), result.getErrorMessage());
        }

    }

}
