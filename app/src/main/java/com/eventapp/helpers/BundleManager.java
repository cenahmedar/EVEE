package com.eventapp.helpers;

import android.os.Bundle;

import java.io.Serializable;

import androidx.fragment.app.Fragment;

public class BundleManager {
    private Bundle bundle;
    public static final String SELECTED_REQUEST = "SELECTED_REQUEST";
    public static final String SELECTED_EVENT = "SELECTED_EVENT";
    public static final String SELECTED_EVENT_KEY = "SELECTED_EVENT_KEY";

    public BundleManager() {
        this.bundle = new Bundle();
    }

    public Bundle getBundle() {
        return bundle;
    }

    public Bundle addSerializable(String name, Serializable object) {
        bundle.putSerializable(name, object);
        return bundle;
    }

    public Serializable getSerializable(Fragment fragment, String name) {
        if (fragment.getArguments() != null)
            return fragment.getArguments().getSerializable(name);
        else
            return null;
    }
}
