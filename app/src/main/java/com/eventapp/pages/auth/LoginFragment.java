package com.eventapp.pages.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.helpers.ProgressBarBuilder;
import com.eventapp.models.User;
import com.eventapp.pages.home.HomePageActivity;

@SuppressLint("NonConstantResourceId")
public class LoginFragment extends BaseFragment implements AuthService.IAuthService {


    private View rootView;

    @BindView(R.id.txEmail)
    EditText txEmail;

    @BindView(R.id.txPassword)
    EditText txPassword;

    private ProgressBarBuilder progressBarBuilder;
    private AuthService authService;


    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, parent, false);
        ButterKnife.bind(this, rootView);
        progressBarBuilder = new ProgressBarBuilder(activity);
        authService = new AuthService(activity, apm, this);
        return rootView;
    }

    @OnClick({R.id.btnRegister, R.id.btnLogin})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                ((AuthActivity) activity).loadFragment(new RegisterFragment(), true);
                break;

            case R.id.btnLogin:
                login();
                break;


        }
    }

    private void login() {
        final String password = txPassword.getText().toString().trim();
        final String email = txEmail.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showToast("Please enter valid email!");
            return;
        }
        progressBarBuilder.show();
        authService.Login(email, password);
    }

    @Override
    public void AuthResponse(AuthService.AuthResponse response, boolean success, User user) {
        progressBarBuilder.hide();
        if (response.equals(AuthService.AuthResponse.Login) && success) {
            startActivity(new Intent(activity, HomePageActivity.class));
            activity.finish();
        }
    }
}