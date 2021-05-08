package com.eventapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eventapp.R;
import com.eventapp.models.EventRatingModel;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class EventRatingModelAdapter extends RecyclerView.Adapter<EventRatingModelAdapter.MyViewHolder> {

    private Context context;
    private List<EventRatingModel> list;
    private LayoutInflater layoutInflater;

    public EventRatingModelAdapter(Context context, List<EventRatingModel> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView desc;
        RatingBar ratingBar;
        CircleImageView hd_image;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            desc = view.findViewById(R.id.tx_desc);
            ratingBar = view.findViewById(R.id.ratingBar);
            hd_image = view.findViewById(R.id.hd_image);

        }
    }


    @NotNull
    @Override
    public EventRatingModelAdapter.MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.rait_row_item, parent, false);
        return new EventRatingModelAdapter.MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(EventRatingModelAdapter.MyViewHolder holder, final int position) {
        final EventRatingModel item = list.get(position);
        holder.name.setText(item.getUser().getName() + " " + item.getUser().getSurName());
        Picasso.get().load(item.getUser().getImage()).fit().centerCrop().placeholder(R.drawable.avatar).error(R.drawable.avatar).into(holder.hd_image);
        holder.ratingBar.setRating(Float.parseFloat(item.getRating().getRateValue()));
        holder.desc.setText(item.getRating().getComment());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


}
