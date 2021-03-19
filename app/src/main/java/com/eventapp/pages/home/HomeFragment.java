package com.eventapp.pages.home;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.adapters.EventViewHolder;
import com.eventapp.dialogs.SettingBottomSheetDialogFragment;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.helpers.BundleManager;
import com.eventapp.helpers.Functions;
import com.eventapp.models.Event;
import com.eventapp.pages.home.event.EventCrudFragment;
import com.eventapp.pages.home.event.EventDetailFragment;
import com.eventapp.pages.home.event.EventListFragment;
import com.eventapp.pages.home.event.MyEventsFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

@SuppressLint({"NonConstantResourceId", "SetTextI18n"})
public class HomeFragment extends BaseFragment {

    private View rootView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.tx_count)
    TextView tx_count;

    private FirebaseRecyclerAdapter<Event, EventViewHolder> adapter;
    private EventService eventService;
    private AuthService authService;

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    private void setToolBar() {
        toolbar.inflateMenu(R.menu.home_menu);
        toolbar.setNavigationIcon(null);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                logOutMenu();
                break;

            case R.id.action_my_list:
                ((HomePageActivity) activity).loadFragment(new MyEventsFragment(), true, false, bundleManager.getBundle());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOutMenu() {
        SettingBottomSheetDialogFragment addPhotoBottomDialogFragment = new SettingBottomSheetDialogFragment();
        addPhotoBottomDialogFragment.show(getChildFragmentManager(), "SettingBottomSheetDialogFragment");
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, parent, false);
        ButterKnife.bind(this, rootView);
        setToolBar();

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
        Date currentTime = Calendar.getInstance().getTime();
        adapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class, R.layout.event_item, EventViewHolder.class, eventService.getRef().orderByChild("dateTime").startAt(Functions.dateTimeToInt(currentTime))) {

            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
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

                tx_count.setText(adapter.getItemCount() + " ");
            }
        };


        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    @OnClick({R.id.btn_search})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                activity.runOnUiThread(() -> ((HomePageActivity) activity).loadFragment(new EventListFragment(), true, false, null));
                break;
        }
    }


}