package com.wedriveu.mobile.tripscheduling.viewmodel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceFactoryImpl;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.service.scheduling.SchedulingServiceCallback;
import com.wedriveu.mobile.tripscheduling.router.SchedulingRouter;
import com.wedriveu.mobile.tripscheduling.view.SchedulingView;
import com.wedriveu.mobile.util.Constants;
import com.wedriveu.mobile.util.location.LocationService;
import com.wedriveu.mobile.util.location.LocationServiceImpl;
import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by Marco on 18/07/2017.
 */
public class SchedulingViewModelImpl extends Fragment implements SchedulingViewModel, SchedulingServiceCallback {

    private SchedulingRouter mRouter;
    private SchedulingService mSchedulingService;
    private SchedulingView mSchedulingView;
    private Place mPlace;

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
        mSchedulingService = ServiceFactoryImpl.getInstance().createSchedulingService();
        LocationService mLocationService = LocationServiceImpl.getInstance(getActivity());
        mLocationService.addLocationListener(mSchedulingService);
    }

    @Override
    public void onSearchVehicleButtonClick() {
        mRouter.showProgressDialog();
        mSchedulingService.findNearestVehicle(mPlace, this);
    }

    @Override
    public void startPlaceAutocomplete() {
        try {
            Intent intent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
            startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e("PLACE", "GooglePlayServices not installed", e.getCause());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e("PLACE", "GooglePlayServices not installed", e.getCause());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                mPlace = PlaceAutocomplete.getPlace(getActivity(), data);
                mSchedulingView = (SchedulingView) getComponentFinder().getView(SchedulingView.TAG);
                mSchedulingView.showSelectedAddress(mPlace);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
               mSchedulingView.renderError(getString(R.string.place_autocomplete_error));
            } else if (resultCode == RESULT_CANCELED) {
                mSchedulingView.renderError(getString(R.string.place_autocomplete_error));
            }
        }
    }

    @Override
    public void onFindNearestVehicleFinished(Vehicle vehicle, String errorMessage) {
        mRouter.dismissProgressDialog();
        if (!TextUtils.isEmpty(errorMessage)) {
            mSchedulingView = (SchedulingView) getComponentFinder().getView(SchedulingView.TAG);
            mSchedulingView.renderError(errorMessage);
        } else {
            mRouter.showBooking(vehicle);
        }
    }

    private ComponentFinder getComponentFinder() {
        ComponentFinder componentFinder = getActivity() != null ? (ComponentFinder) getActivity() : null;
        return componentFinder;
    }

}
