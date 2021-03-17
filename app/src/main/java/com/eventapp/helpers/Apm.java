package com.eventapp.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.eventapp.models.User;
import com.google.gson.Gson;

import static android.content.Context.MODE_PRIVATE;

public class Apm {
    private static final String APP_SHARED_PREFS = "auth-preferences";
    private static final String SHARED_USER = "SHARED_USER";

    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;
    private Gson gson;
    private Context context;


    @SuppressLint("CommitPrefEdits")
    public Apm(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
        this.context = context;
        gson = new Gson();
    }

    @SuppressLint("CommitPrefEdits")
    public Apm() {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
        gson = new Gson();
    }

    public void postUser(User user) {
        String json = gson.toJson(user);
        prefsEditor.putString(SHARED_USER, json);
        prefsEditor.commit();
    }

    public User getUser() {
        String json = appSharedPrefs.getString(SHARED_USER, "");
        return gson.fromJson(json, User.class);

    }


}
