package com.eventapp.pages.home.event;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.adapters.EventViewHolder;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.helpers.BundleManager;
import com.eventapp.helpers.Functions;
import com.eventapp.models.Event;
import com.eventapp.pages.home.HomePageActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

@SuppressLint({"NonConstantResourceId", "SetTextI18n"})
public class MyEventsFragment extends BaseFragment {

    private View rootView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    private FirebaseRecyclerAdapter<Event, EventViewHolder> adapter;
    private EventService eventService;
    private AuthService authService;

    private void setToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_action_back_white);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        toolbar.setTitle("My Events");
        toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
    }

    private void setTableLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Created"));
        tabLayout.addTab(tabLayout.newTab().setText("Joined"));
        tabLayout.addTab(tabLayout.newTab().setText("Passed"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LoadList(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_events, parent, false);
        ButterKnife.bind(this, rootView);
        setToolBar();
        setTableLayout();
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
        LoadList(tabLayout.getSelectedTabPosition());

    }

    private void LoadList(int selectedTabPosition) {
        Query query;
        Date currentTime = Calendar.getInstance().getTime();

        switch (selectedTabPosition) {
            case 1:
                //joined
                floatingActionButton.setVisibility(View.GONE);
                query = eventService.getRef().orderByChild("dateTime").startAt(Functions.dateTimeToInt(currentTime));
                break;
            case 2:
                //passed
                floatingActionButton.setVisibility(View.GONE);
                query = eventService.getRef().orderByChild("dateTime").endAt(Functions.dateTimeToInt(currentTime));
                break;
            default:
                //created
                floatingActionButton.setVisibility(View.VISIBLE);
                query = eventService.getRef().orderByChild("userKey").equalTo(authService.getMyKey());
                break;
        }


        adapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class, R.layout.event_item, EventViewHolder.class, query) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
                if (selectedTabPosition == 0 || (model.getParticipants() != null && model.getParticipants().contains(authService.getMyKey()))) {
                    viewHolder.tx_title.setText(model.getName().trim());
                    viewHolder.tx_location.setText(model.getAddress().getAddress());
                    viewHolder.tx_day.setText(Functions.getDay(model.getDate()));
                    viewHolder.tx_month.setText(Functions.getMonthName(model.getDate()));
                    viewHolder.tx_count.setText(model.getUsersCount() + "");
                    Picasso.get().load(model.getImage()).into(viewHolder.card_image);

                    viewHolder.setItemClickListner((view, position1) -> {
                        bundleManager = new BundleManager();
                        bundleManager.addSerializable(BundleManager.SELECTED_EVENT_KEY, adapter.getRef(position1).getKey());
                        activity.runOnUiThread(() -> ((HomePageActivity) activity).loadFragment(new EventDetailFragment(), true, false, bundleManager.getBundle()));
                    });
                } else {
                    viewHolder.ln_main.setVisibility(View.GONE);
                }
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