package com.eventapp.adapters;

import android.annotation.SuppressLint;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eventapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    @BindView(R.id.tx_title)
    public TextView tx_title;

    @BindView(R.id.tx_day)
    public TextView tx_day;

    @BindView(R.id.tx_month)
    public TextView tx_month;

    @BindView(R.id.tx_location)
    public TextView tx_location;

    @BindView(R.id.tx_count)
    public TextView tx_count;

    @BindView(R.id.card_image)
    public ImageView card_image;

    @BindView(R.id.ln_main)
    public LinearLayout ln_main;


    private IEventViewHolderListener eventViewHolderListener;


    public EventViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        eventViewHolderListener.onEventClick(v, getAdapterPosition());

    }

    public void setItemClickListner(IEventViewHolderListener itemClickListner) {
        this.eventViewHolderListener = itemClickListner;
    }


    public interface IEventViewHolderListener {
        void onEventClick(View view, int position);
    }

}
