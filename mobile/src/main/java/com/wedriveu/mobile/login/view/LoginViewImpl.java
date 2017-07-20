package com.wedriveu.mobile.login.view;

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
import com.wedriveu.mobile.login.viewmodel.LoginViewModel;
import com.wedriveu.mobile.util.Constants;

public class LoginViewImpl extends Fragment implements LoginView {

    private Button mLoginButton;
    private EditText mUsername;
    private EditText mPassword;

    public static LoginViewImpl newInstance(String viewModelId) {
        LoginViewImpl fragment = new LoginViewImpl();
        Bundle arguments = new Bundle();
        arguments.putString(Constants.VIEW_MODEL_ID, viewModelId);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_view, container, false);
        setupUIComponents(view);
        renderView();
        return view;
    }

    private void setupUIComponents(View view){
        mUsername = (EditText) view.findViewById(R.id.editTextUsername);
        mPassword = (EditText) view.findViewById(R.id.editTextPassword);
        mLoginButton =  (Button) view.findViewById(R.id.loginButton);
    }

    @Override
    public void renderView() {
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
