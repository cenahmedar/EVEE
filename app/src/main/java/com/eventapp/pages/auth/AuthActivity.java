package com.eventapp.pages.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.eventapp.R;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean start_with_login_fragment = false;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            start_with_login_fragment = bundle.getBoolean("start_with_login_fragment");

        if (start_with_login_fragment)
            loadFragment(new LoginFragment(), false);
        else
            loadFragment(new SplashFragment(), false);
        // setStatusBarGradiant(this);

    }

    public void loadFragment(Fragment fragment, boolean isChildFragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_activity_home_container, fragment);
        if (isChildFragment)
            transaction.addToBackStack("");
        transaction.commit();
    }
}