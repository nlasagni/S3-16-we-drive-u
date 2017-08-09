package com.wedriveu.mobile.tripscheduling.viewmodel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.store.StoreFactoryImpl;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.mobile.store.VehicleStore;
import com.wedriveu.mobile.tripscheduling.router.SchedulingRouter;
import com.wedriveu.mobile.tripscheduling.view.SchedulingView;
import com.wedriveu.mobile.util.Constants;
import com.wedriveu.mobile.util.location.LocationService;
import com.wedriveu.mobile.util.location.LocationServiceImpl;

import static android.app.Activity.RESULT_CANCELED;

/**
 * @author Marco on 18/07/2017.
 * @author Nicola Lasagni on 29/07/2017
 */
public class SchedulingViewModelImpl extends Fragment implements SchedulingViewModel {

    private SchedulingRouter mRouter;
    private SchedulingService mSchedulingService;
    private SchedulingView mSchedulingView;
    private VehicleStore mVehicleStore;
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
        Activity activity = getActivity();
        UserStore userStore = StoreFactoryImpl.getInstance().createUserStore(getContext());
        mSchedulingService = ServiceFactoryImpl.getInstance().createSchedulingService(activity, userStore);
        LocationService mLocationService = LocationServiceImpl.getInstance(activity);
        mVehicleStore = StoreFactoryImpl.getInstance().createVehicleStore(getContext());
        mLocationService.addLocationListener(mSchedulingService);
    }

    @Override
    public void onSearchVehicleButtonClick() {
        mRouter.showProgressDialog();
        mSchedulingService.findNearestVehicle(mPlace, new ServiceOperationCallback<Vehicle>() {
            @Override
            public void onServiceOperationFinished(Vehicle result, String errorMessage) {
                onFindNearestVehicleFinished(result, errorMessage);
            }
        });
    }

    @Override
    public void startPlaceAutocomplete() {
        try {
            Intent intent = new PlaceAutocomplete
                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(getActivity());
            getActivity().startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(SchedulingViewModel.TAG, "GooglePlayServices not installed", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            mPlace = PlaceAutocomplete.getPlace(getActivity(), data);
            mSchedulingView = (SchedulingView) getComponentFinder().getView(SchedulingView.TAG);
            mSchedulingView.showSelectedAddress(mPlace);
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
           mSchedulingView.renderError(getString(R.string.place_autocomplete_error));
        } else if (resultCode == RESULT_CANCELED) {
            mSchedulingView.renderError(getString(R.string.place_autocomplete_error));
        }
    }

    private void onFindNearestVehicleFinished(Vehicle vehicle, String errorMessage) {
        mRouter.dismissProgressDialog();
        if (!TextUtils.isEmpty(errorMessage)) {
            mSchedulingView = (SchedulingView) getComponentFinder().getView(SchedulingView.TAG);
            mSchedulingView.renderError(errorMessage);
        } else {
            mVehicleStore.storeVehicle(vehicle);
            mRouter.showBooking();
        }
    }

    private ComponentFinder getComponentFinder() {
        return ((ComponentFinder) getActivity());
    }

}
