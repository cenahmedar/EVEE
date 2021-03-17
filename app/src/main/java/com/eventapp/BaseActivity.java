package com.eventapp;

import android.os.Bundle;
import android.widget.Toast;

import com.eventapp.helpers.Apm;
import com.eventapp.helpers.BundleManager;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    public Apm apm;
    protected MySuperAppApplication mMyApp;
    protected BundleManager bundleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());

        ButterKnife.bind(this);
        //  initTheme();
        apm = new Apm(this);
        mMyApp = (MySuperAppApplication) this.getApplicationContext();
        bundleManager = new BundleManager();

    }

    protected abstract int getLayoutResourceId();

    protected void onResume() {
        super.onResume();
        mMyApp.setCurrentActivity(this);
        mMyApp.ProgressBarBuilder();
    }

    protected void showToast(String msg) {
        Toast.makeText(mMyApp, msg, Toast.LENGTH_LONG).show();
    }

    protected void showProgress() {
        MySuperAppApplication.showProgress();
    }

    protected void hideProgress() {
        MySuperAppApplication.hideProgess();
    }

}
