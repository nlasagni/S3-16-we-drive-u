package com.wedriveu.mobile.booking.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.booking.viewmodel.BookingViewModel;

/**
 * @author Nicola Lasagni on 19/08/2017.
 */
public class EnterVehicleView extends Fragment {

    public static final String ID = EnterVehicleView.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_vehicle, container, false);
        view.findViewById(R.id.enter_vehicle_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingViewModel viewModel = getViewModel();
                if (viewModel != null) {
                    viewModel.onEnterVehicleButtonClick();
                }
            }
        });
        return view;
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
