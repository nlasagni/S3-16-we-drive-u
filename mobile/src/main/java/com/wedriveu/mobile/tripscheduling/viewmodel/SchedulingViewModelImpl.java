package com.wedriveu.mobile.tripscheduling.viewmodel;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceFactoryImpl;
import com.wedriveu.mobile.service.scheduling.SchedulingService;
import com.wedriveu.mobile.service.scheduling.SchedulingServiceCallback;
import com.wedriveu.mobile.tripscheduling.router.SchedulingRouter;

/**
 * Created by Marco on 18/07/2017.
 */
public class SchedulingViewModelImpl extends Fragment implements SchedulingViewModel, SchedulingServiceCallback {

    private String mViewId;
    private SchedulingRouter mRouter;
    private SchedulingService mSchedulingService;


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

    }

    @Override
    public void onFindNearestVehicleFinished(Vehicle vehicle) {

    }
}
