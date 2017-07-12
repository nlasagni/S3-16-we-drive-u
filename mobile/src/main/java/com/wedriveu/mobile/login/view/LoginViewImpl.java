package com.wedriveu.mobile.login.view;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.wedriveu.mobile.R;
import com.wedriveu.mobile.app.Application;
import com.wedriveu.mobile.login.viewmodel.LoginViewModel;

public class LoginViewImpl extends Fragment implements LoginView {

    private Button mLoginButton;
    private LoginViewModel mViewModel;
    private EditText mUsername;
    private EditText mPassword;

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
        mViewModel = (LoginViewModel) ((Application) getActivity()).getView(LoginViewModel.LOGIN_VIEW_MODEL_TAG);
    }

    @Override
    public void renderView() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.onLoginButtonClick(mUsername.getText().toString(), mPassword.getText().toString());
            }
        });
    }

    @Override
    public void renderError(String message) {
        Log.e("ERROR", message);
    }

}
