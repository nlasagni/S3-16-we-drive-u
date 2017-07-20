package com.wedriveu.mobile.tripscheduling.viewmodel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceFactoryImpl;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.service.scheduling.SchedulingServiceCallback;
import com.wedriveu.mobile.tripscheduling.router.SchedulingRouter;
import com.wedriveu.mobile.tripscheduling.view.SchedulingView;
import com.wedriveu.mobile.tripscheduling.view.SchedulingViewImpl;
import com.wedriveu.mobile.util.Constants;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by Marco on 18/07/2017.
 */
public class SchedulingViewModelImpl extends Fragment implements SchedulingViewModel, SchedulingServiceCallback {

    private String mViewId;
    private SchedulingRouter mRouter;
    private SchedulingService mSchedulingService;
    private SchedulingView mSchedulingView;


    public static SchedulingViewModelImpl newInstance(String viewId) {
        SchedulingViewModelImpl fragment = new SchedulingViewModelImpl();
        fragment.setRetainInstance(true);
        fragment.setViewId(viewId);
        return fragment;
    }

    private void setViewId(String viewId) {
        mViewId = viewId;
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
    }

    @Override
    public void onSearchVehicleButtonClick(String address) {
        mSchedulingService.findNearestVehicle(address);
    }

    @Override
    public void onFindNearestVehicleFinished(Vehicle vehicle) {

    }

    @Override
    public void startPlaceAutocomplete() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
            startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Log.e("PLACE", e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Log.e("PLACE", e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                mSchedulingView = (SchedulingView) getFragmentManager().findFragmentByTag(SchedulingView.TAG);
                mSchedulingView.showSelectedAddress(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
               mSchedulingView.renderError(getString(R.string.place_autocomplete_error));
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                mSchedulingView.renderError(getString(R.string.place_autocomplete_error));
            }
        }
    }

}
