package com.eventapp.pages.home.event;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.adapters.EventViewHolder;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.helpers.BundleManager;
import com.eventapp.helpers.Functions;
import com.eventapp.models.Event;
import com.eventapp.models.User;
import com.eventapp.pages.home.HomePageActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressLint("NonConstantResourceId")
public class MyEventsFragment extends BaseFragment {

    private View rootView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<Event, EventViewHolder> adapter;
    private EventService eventService;
    private AuthService authService;

    private void setToolBar() {
        toolbar.setTitle(null);
        toolbar.setNavigationIcon(R.drawable.ic_action_close);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_events, parent, false);
        ButterKnife.bind(this, rootView);
        setToolBar();
        floatingActionButton.setColorFilter(Color.WHITE);

        eventService = new EventService();
        authService = new AuthService(activity, apm);
        init();
        return rootView;
    }

    private void init() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        LoadList();

    }

    private void LoadList() {
        Query query = eventService.getRef().orderByChild("userKey").equalTo(authService.getMyKey());
        adapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class, R.layout.event_item, EventViewHolder.class, query) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
                viewHolder.tx_title.setText(model.getName().trim());
                viewHolder.tx_location.setText(model.getAddress().getAddress());
                viewHolder.tx_day.setText(Functions.getDay(model.getDate()));
                viewHolder.tx_month.setText(Functions.getMonthName(model.getDate()));

                Picasso.get().load(model.getImage()).into(viewHolder.card_image);

                viewHolder.setItemClickListner(new EventViewHolder.IEventViewHolderListener() {
                    @Override
                    public void onEventClick(View view, int position) {

                        bundleManager = new BundleManager();
                       // bundleManager.addSerializable(BundleManager.SELECTED_EVENT, model);
                        bundleManager.addSerializable(BundleManager.SELECTED_EVENT_KEY, adapter.getRef(position).getKey());
                        activity.runOnUiThread(() -> ((HomePageActivity) activity).loadFragment(new EventDetailFragment(), true, false, bundleManager.getBundle()));
                    }
                });

/*

                // HERE WHAT CORRESPONDS TO JOIN
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(model.getUserKey())
                        .addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            if (postSnapshot.getKey().equals(model.getUserKey())) {
                                                model.setUser(dataSnapshot.getValue(User.class));

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NotNull DatabaseError databaseError) {

                                    }
                                }
                        );

*/

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    @OnClick({R.id.floatingActionButton})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton:
                activity.runOnUiThread(() -> ((HomePageActivity) activity).loadFragment(new EventCrudFragment(), true, false, null));
                break;

        }
    }




}