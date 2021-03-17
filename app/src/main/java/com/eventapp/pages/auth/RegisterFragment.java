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
import android.widget.Toast;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.helpers.ProgressBarBuilder;
import com.eventapp.models.User;
import com.eventapp.pages.home.HomePageActivity;

import java.util.RandomAccess;

@SuppressLint("NonConstantResourceId")
public class RegisterFragment extends BaseFragment implements AuthService.IAuthService {


    private View rootView;


    @BindView(R.id.txName)
    EditText txName;

    @BindView(R.id.txSurname)
    EditText txSurname;

    @BindView(R.id.txEmail)
    EditText txEmail;

    @BindView(R.id.txPassword)
    EditText txPassword;

    private ProgressBarBuilder progressBarBuilder;
    private AuthService authService;

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_register, parent, false);
        ButterKnife.bind(this, rootView);
        progressBarBuilder = new ProgressBarBuilder(activity);
        authService = new AuthService(activity, apm, this);
        return rootView;
    }


    @OnClick({R.id.btnRegister, R.id.btnLogin})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                register();
                break;

            case R.id.btnLogin:
                activity.onBackPressed();
                break;

        }
    }

    private void register() {
        final String password = txPassword.getText().toString().trim();
        final String email = txEmail.getText().toString().trim();
        final String name = txName.getText().toString().trim();
        final String surname = txSurname.getText().toString().trim();
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showToast("Please fill all fields");
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showToast("Please enter valid email!");
            return;
        }
        progressBarBuilder.show();
        authService.Register(name, surname, email, password);

    }

    @Override
    public void AuthResponse(AuthService.AuthResponse response, boolean success, User user) {
        progressBarBuilder.hide();
        if (response.equals(AuthService.AuthResponse.Register) && success) {
            startActivity(new Intent(activity, HomePageActivity.class));
            activity.finish();
        }

    }
}