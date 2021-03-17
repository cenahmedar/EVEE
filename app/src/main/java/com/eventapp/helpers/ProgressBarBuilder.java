package com.eventapp.helpers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.eventapp.R;

import java.util.Objects;

public class ProgressBarBuilder {
    private Context context;
    private Dialog dialogTransparent;


    public ProgressBarBuilder(Context context) {
        this.context = context;
        dialogTransparent = new Dialog(context, android.R.style.Theme_Black);
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.remove_border, null);
        dialogTransparent.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialogTransparent.getWindow()).setBackgroundDrawableResource(R.color.transparent_grey);
        dialogTransparent.setContentView(view);
    }


    public void show() {
        dialogTransparent.show();
    }

    public void hide() {
        dialogTransparent.dismiss();
    }
}
