package com.wedriveu.mobile.tripscheduling.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.login.view.LoginViewImpl;
import com.wedriveu.mobile.login.viewmodel.LoginViewModel;
import com.wedriveu.mobile.tripscheduling.viewmodel.SchedulingViewModel;
import com.wedriveu.mobile.util.Constants;

/**
 * Created by Marco on 18/07/2017.
 */
public class SchedulingViewImpl extends Fragment implements SchedulingView{
    private EditText mAddress;
    private Button mScheduleButton;

    public static SchedulingViewImpl newInstance(String viewModelId) {
        SchedulingViewImpl fragment = new SchedulingViewImpl();
        Bundle arguments = new Bundle();
        arguments.putString(Constants.VIEW_MODEL_ID, viewModelId);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scheduling_view, container, false);
        setupUIComponents(view);
        renderView();
        return view;
    }

    private void setupUIComponents(View view){
        mAddress = (EditText) view.findViewById(R.id.destinationAddressText);
        mScheduleButton = (Button) view.findViewById(R.id.scheduleButton);
    }


    @Override
    public void renderView() {
        mScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAddress(mAddress.getText().toString());
            }
        });

    }

    private void sendAddress(String address) {
        SchedulingViewModel viewModel = getViewModel();
        if (viewModel != null) {
            viewModel.onSearchVehicleButtonClick(address);
        }
    }
    private SchedulingViewModel getViewModel() {
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            String viewModelId = getArguments().getString(Constants.VIEW_MODEL_ID);
            return (SchedulingViewModel) componentFinder.getViewModel(viewModelId);
        }
        return null;
    }

    @Override
    public void renderError(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle(R.string.common_warning)
                .setMessage(message)
                .setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }
}
