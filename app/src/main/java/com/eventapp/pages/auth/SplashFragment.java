package com.eventapp.pages.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.models.User;
import com.eventapp.pages.home.HomePageActivity;


public class SplashFragment extends BaseFragment implements AuthService.IAuthService {

    private AuthService authService;

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash, parent, false);
        authService = new AuthService(activity, apm, this);
        init();
        return rootView;
    }

    private void init() {
        if (this.apm.getUser() != null) {
            new Handler().postDelayed(() -> authService.Login(apm.getUser().getEmail(), apm.getUser().getPassword()), 2000);
        } else {
            new Handler().postDelayed(() -> ((AuthActivity) activity).loadFragment(new LoginFragment(), false), 2000);
        }
    }


    @Override
    public void AuthResponse(AuthService.AuthResponse response, boolean success, User user) {
        if (response.equals(AuthService.AuthResponse.Login)) {
            if(success){
                startActivity(new Intent(activity, HomePageActivity.class));
                activity.finish();
            }else {
                ((AuthActivity) activity).loadFragment(new LoginFragment(), false);
            }

        }
    }
}