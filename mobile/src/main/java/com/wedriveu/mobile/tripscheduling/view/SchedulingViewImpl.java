package com.wedriveu.mobile.tripscheduling.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.google.android.gms.location.places.Place;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModel;
import com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModelImpl;

/**
 * The {@linkplain SchedulingView} implementation.
 *
 * @author Marco on 18/07/2017.
 * @author Nicola Lasagni
 */
public class SchedulingViewImpl extends Fragment implements SchedulingView, View.OnClickListener {

    private CheckBox mUseMyPosition;
    private EditText mPickUpAddressEditText;
    private EditText mDestinationAddressEditText;
    private Button mSearchVehicleButton;

    /**
     * New instance of a {@link SchedulingView}.
     *
     * @return the scheduling view
     */
    public static SchedulingViewImpl newInstance() {
        return new SchedulingViewImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scheduling, container, false);
        setupUIComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        renderView();
    }

    private void setupUIComponents(View view){
        mUseMyPosition = (CheckBox) view.findViewById(R.id.use_my_position);
        mPickUpAddressEditText = (EditText) view.findViewById(R.id.pick_up_address);
        mDestinationAddressEditText = (EditText) view.findViewById(R.id.destination_address);
        mSearchVehicleButton = (Button) view.findViewById(R.id.search_vehicle_button);
        mUseMyPosition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPickUpAddressEditText.setEnabled(!isChecked);
                mPickUpAddressEditText.setFocusable(!isChecked);
            }
        });
    }

    @Override
    public void renderView() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.scheduling_title);
        mSearchVehicleButton.setOnClickListener(this);
        mPickUpAddressEditText.setOnClickListener(this);
        mDestinationAddressEditText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.pick_up_address || viewId == R.id.destination_address) {
            getAddress(viewId == R.id.destination_address);
        } else if (viewId == R.id.search_vehicle_button) {
            if (!mUseMyPosition.isChecked() &&
                    mPickUpAddressEditText.getText().toString().trim().isEmpty()) {
                renderError(getString(R.string.pick_up_position_not_filled));
            } else if(mDestinationAddressEditText.getText().toString().trim().length() == 0) {
                renderError(getString(R.string.destination_address_not_filled));
            } else {
                startVehicleSearch();
            }
        }
    }

    private void getAddress(boolean forDestination) {
        SchedulingViewModel viewModel = getViewModel();
        if(viewModel != null ){
            viewModel.startPlaceAutocomplete(forDestination);
        }
    }

    private void startVehicleSearch() {
        SchedulingViewModel viewModel = getViewModel();
        if (viewModel != null) {
            viewModel.onSearchVehicleButtonClick(mUseMyPosition.isChecked());
        }
    }

    private SchedulingViewModel getViewModel() {
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            return (SchedulingViewModelImpl) componentFinder.getViewModel(SchedulingViewModel.ID);
        }
        return null;
    }

    @Override
    public void renderError(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.common_warning)
                .setMessage(message)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void showPickUpAddress(Place address) {
        mPickUpAddressEditText.setText(address.getAddress());
    }

    @Override
    public void showDestinationAddress(Place address) {
        mDestinationAddressEditText.setText(address.getAddress());
    }

}
