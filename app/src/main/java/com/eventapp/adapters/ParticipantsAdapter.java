package com.eventapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eventapp.R;
import com.eventapp.models.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
@SuppressLint("SetTextI18n")
public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.MyViewHolder> {

    private Context context;
    private List<User> list;
    private LayoutInflater layoutInflater;

    public ParticipantsAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CircleImageView hd_image;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            hd_image = view.findViewById(R.id.hd_image);

        }
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.part_row_item, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final User user = list.get(position);
        holder.name.setText(user.getName() + " " + user.getSurName());
        Picasso.get().load(user.getImage()).fit().centerCrop().placeholder(R.drawable.avatar).error(R.drawable.avatar).into(holder.hd_image);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


}
