package com.wedriveu.mobile.login.view;

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
import android.widget.EditText;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.ComponentFinder;
import com.wedriveu.mobile.login.viewmodel.LoginViewModel;
import com.wedriveu.mobile.util.Constants;

/**
 * The effective login view implementation.
 */
public class LoginViewImpl extends Fragment implements LoginView {

    private Button mLoginButton;
    private EditText mUsername;
    private EditText mPassword;

    /**
     * New instance of a {@linkplain LoginView}.
     *
     * @param viewModelId the view model id
     * @return the login view
     */
    public static LoginViewImpl newInstance(String viewModelId) {
        LoginViewImpl fragment = new LoginViewImpl();
        Bundle arguments = new Bundle();
        arguments.putString(Constants.VIEW_MODEL_ID, viewModelId);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        setupUIComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        renderView();
    }

    private void setupUIComponents(View view){
        mUsername = (EditText) view.findViewById(R.id.username);
        mPassword = (EditText) view.findViewById(R.id.password);
        mLoginButton =  (Button) view.findViewById(R.id.login_button);
    }

    @Override
    public void renderView() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.we_drive_you_title);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(mUsername.getText().toString(), mPassword.getText().toString());
            }
        });
    }

    private void login(String username, String password) {
        LoginViewModel viewModel = getViewModel();
        if (viewModel != null) {
            viewModel.onLoginButtonClick(username, password);
        }
    }

    private LoginViewModel getViewModel() {
        ComponentFinder componentFinder = (ComponentFinder) getActivity();
        if (componentFinder != null) {
            String viewModelId = getArguments().getString(Constants.VIEW_MODEL_ID);
            return (LoginViewModel) componentFinder.getViewModel(viewModelId);
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
