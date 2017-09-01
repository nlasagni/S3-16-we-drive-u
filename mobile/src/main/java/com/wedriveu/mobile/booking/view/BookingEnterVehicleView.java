package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.view.View;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.booking.viewmodel.BookingViewModel;
import com.wedriveu.mobile.booking.viewmodel.model.BookingSummaryPresentationModel;

/**
 * The view that allow the user to enter into the booked vehicle.
 *
 * @author Nicola Lasagni on 19/08/2017.
 */
public class BookingEnterVehicleView extends AbstractBookingView {

    /**
     * The id of this view.
     */
    public static final String ID = BookingEnterVehicleView.class.getSimpleName();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_enter_vehicle;
    }

    @Override
    protected void setupViewComponents(View view, Bundle savedInstanceState) {
        view.findViewById(R.id.enter_vehicle_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingViewModel viewModel = getViewModel();
                if (viewModel != null) {
                    viewModel.onEnterVehicleButtonClick();
                }
            }
        });
    }

    @Override
    protected int getActionBarTitle() {
        return R.string.app_name;
    }

    @Override
    protected void renderPresentationModel(BookingSummaryPresentationModel presentationModel) {
        //Empty
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
