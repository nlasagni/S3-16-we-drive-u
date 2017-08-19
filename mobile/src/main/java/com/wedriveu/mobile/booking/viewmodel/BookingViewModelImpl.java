package com.wedriveu.mobile.booking.viewmodel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.booking.router.BookingRouter;
import com.wedriveu.mobile.booking.viewmodel.model.BookingPresentationModel;
import com.wedriveu.mobile.booking.view.BookingView;
import com.wedriveu.mobile.model.User;
import com.wedriveu.mobile.model.Vehicle;
import com.wedriveu.mobile.service.ServiceFactory;
import com.wedriveu.mobile.service.ServiceFactoryImpl;
import com.wedriveu.mobile.service.ServiceOperationCallback;
import com.wedriveu.mobile.service.ServiceResult;
import com.wedriveu.mobile.service.booking.BookingService;
import com.wedriveu.mobile.service.vehicle.EnterVehicleService;
import com.wedriveu.mobile.store.BookingStore;
import com.wedriveu.mobile.store.StoreFactoryImpl;
import com.wedriveu.mobile.store.UserStore;
import com.wedriveu.mobile.store.VehicleStore;
import com.wedriveu.shared.rabbitmq.message.CreateBookingResponse;

/**
 * @author Nicola Lasagni on 29/07/2017.
 */
public class BookingViewModelImpl extends Fragment implements BookingViewModel {

    private static final int RENDER_DELAY = 200;

    private BookingRouter mRouter;
    private BookingService mBookingService;
    private EnterVehicleService mEnterVehicleService;
    private BookingStore mBookingStore;
    private VehicleStore mVehicleStore;

    public static BookingViewModelImpl newInstance() {
        BookingViewModelImpl fragment = new BookingViewModelImpl();
        fragment.setRetainInstance(true);
        return fragment;
    }

    // This deprecated method has been chosen because the minTargetVersion is 19.
    // Switch to minTargetVersion >= 23 in order to use the new onAttach method.
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRouter = (BookingRouter) activity;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BookingStore bookingStore = StoreFactoryImpl.getInstance().createBookingStore(getContext());
        UserStore userStore = StoreFactoryImpl.getInstance().createUserStore(getContext());
        mBookingService = ServiceFactoryImpl.getInstance().createBookingService(getActivity(), userStore);
        mEnterVehicleService = ServiceFactoryImpl.getInstance().createEnterVehicleService(getActivity(), userStore, bookingStore);
        mVehicleStore = StoreFactoryImpl.getInstance().createVehicleStore(getContext());
        mBookingStore = StoreFactoryImpl.getInstance().createBookingStore(getContext());
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
        mRouter.showProgressDialog();
        mBookingService.acceptBooking(mBookingStore.getBooking(),
                new ServiceOperationCallback<CreateBookingResponse>() {
            @Override
            public void onServiceOperationFinished(ServiceResult<CreateBookingResponse> result) {
                mRouter.dismissProgressDialog();
                if (result.getResult() == null || !result.getResult().isSuccess()) {
                    BookingView bookingView = getBookingView();
                    bookingView.renderError(result.getErrorMessage());
                } else {
                    Vehicle vehicle = mVehicleStore.getVehicle();
                    Vehicle newVehicle =
                            new Vehicle(vehicle.getLicencePlate(),
                                    vehicle.getVehicleName(),
                                    vehicle.getDescription(),
                                    vehicle.getPictureURL(),
                                    result.getResult().getDriveTimeToUser(),
                                    result.getResult().getDriveTimeToDestination());
                    mVehicleStore.storeVehicle(newVehicle);
                    mRouter.showBookingAcceptedView();

                }
            }
        });
    }

    @Override
    public void onDeclineButtonClick() {
        mBookingStore.clear();
        mVehicleStore.clear();
        mRouter.goBackToTripScheduling();
    }

    @Override
    public void onEnterVehicleButtonClick() {

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
