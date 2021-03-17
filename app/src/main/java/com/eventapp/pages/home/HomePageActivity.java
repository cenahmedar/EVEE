package com.eventapp.pages.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.eventapp.BaseActivity;
import com.eventapp.R;

@SuppressLint("NonConstantResourceId")
public class HomePageActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadFragment(new HomeFragment(), false, false, null);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home_page;
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack,boolean popBackStack, Bundle bundle) {
        if (bundle != null)
            fragment.setArguments(bundle);

        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (popBackStack)
            getSupportFragmentManager().popBackStack();

        transaction.replace(R.id.home_activity_home_container, fragment);

        if (addToBackStack)
            transaction.addToBackStack(null);

        transaction.commit();
    }

    public void addFragment(Fragment current, Fragment fragment, boolean addToBackStack, boolean popBackStack, Bundle bundle) {
        if (bundle != null)
            fragment.setArguments(bundle);

        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (popBackStack)
            getSupportFragmentManager().popBackStack();

        transaction.hide(current);
        transaction.add(R.id.home_activity_home_container, fragment);

        if (addToBackStack)
            transaction.addToBackStack(null);

        transaction.commit();
    }

    public void refreshFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        transaction.replace(R.id.home_activity_home_container, fragment);
        transaction.addToBackStack("");
        transaction.commit();
    }

}