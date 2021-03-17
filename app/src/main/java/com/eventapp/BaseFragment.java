package com.eventapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventapp.helpers.Apm;
import com.eventapp.helpers.BundleManager;
import com.eventapp.helpers.ProgressBarBuilder;

import org.jetbrains.annotations.NotNull;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected Apm apm;
    protected Activity activity;
    protected BundleManager bundleManager;
    protected MySuperAppApplication mySuperAppApplication;
    protected ProgressBarBuilder progressBarBuilder;

    public void onCreate(Bundle savedInstanceState) {
        activity = getActivity();
        apm = new Apm(activity);
        mySuperAppApplication = ((MySuperAppApplication) getActivity().getApplication());
        bundleManager = new BundleManager();
        progressBarBuilder = new ProgressBarBuilder(activity);
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        View view = provideYourFragmentView(inflater, parent, savedInstanseState);
        apm = new Apm(getActivity());
        return view;
    }

    public abstract View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState);

    protected void showToast(String msg) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }

}
