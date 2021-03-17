package com.eventapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.eventapp.helpers.ProgressBarBuilder;

import java.util.ArrayList;
import java.util.Objects;

public class MySuperAppApplication extends Application {
    private static Application instance;
    public ProgressBarBuilder progressBarBuilder;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        progressBarBuilder = new ProgressBarBuilder(this);

        ProgressBarBuilder();
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }


    private Activity mCurrentActivity = null;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    private static Dialog dialogTransparent;

    @SuppressLint("InflateParams")
    public void ProgressBarBuilder() {
        if (getCurrentActivity() != null) {
            dialogTransparent = new Dialog(getCurrentActivity(), android.R.style.Theme_Black);
            View view = LayoutInflater.from(MySuperAppApplication.getContext()
                    .getApplicationContext()).inflate(R.layout.remove_border, null);
            dialogTransparent.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Objects.requireNonNull(dialogTransparent.getWindow()).setBackgroundDrawableResource(R.color.transparent_grey);
            dialogTransparent.setContentView(view);
        }

    }


    public static void showProgress() {

        dialogTransparent.show();
    }

    public static void hideProgess() {
        dialogTransparent.dismiss();
    }


}
