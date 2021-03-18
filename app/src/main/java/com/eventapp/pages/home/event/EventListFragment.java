package com.eventapp.pages.home.event;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.adapters.EventViewHolder;
import com.eventapp.dialogs.EventSearchDialog;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.helpers.BundleManager;
import com.eventapp.helpers.Functions;
import com.eventapp.models.AddressModel;
import com.eventapp.models.Event;
import com.eventapp.models.EventFilter;
import com.eventapp.pages.home.HomePageActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

@SuppressLint({"NonConstantResourceId", "SetTextI18n"})
public class EventListFragment extends BaseFragment {


    private View rootView;
    private EventFilter eventFilter;


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private FirebaseRecyclerAdapter<Event, EventViewHolder> adapter;
    private EventService eventService;
    private AuthService authService;


    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.even_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                activity.runOnUiThread(() -> ((HomePageActivity) activity).loadFragment(new EventMapFragment(), true, false, bundleManager.getBundle()));
                break;

            case R.id.action_filter:
                openSearchDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        eventFilter = new EventFilter();
    }


    private void setToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        ((AppCompatActivity) activity).setSupportActionBar(toolbar);
        toolbar.setTitle(null);
        toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
        toolbar.inflateMenu(R.menu.even_list_menu);
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_list, parent, false);
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
        Query query = eventService.getRef().orderByChild("dateTime").startAt(Functions.dateTimeToInt(currentTime));
        adapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class, R.layout.event_item, EventViewHolder.class, query) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
                if (eventFilter.getCityName() != null && !eventFilter.getCityName().equals(model.getAddress().getCity())) {
                    viewHolder.ln_main.setVisibility(View.GONE);
                } else if (eventFilter.getTypeID() != -1 && eventFilter.getTypeID() != model.getTypeID()) {
                    viewHolder.ln_main.setVisibility(View.GONE);
                } else if (eventFilter.getPriceTo() != null) {
                    if (eventFilter.getPriceTo().equals("0") && (!model.isFree()))
                        viewHolder.ln_main.setVisibility(View.GONE);
                    else if (model.getPrice() != null) {
                        if (Double.compare(model.getPrice(), Double.parseDouble(eventFilter.getPriceTo())) > 0)
                            viewHolder.ln_main.setVisibility(View.GONE);
                    }

                }

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

            }

        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    private void openSearchDialog() {
        EventSearchDialog exampleDialog = new EventSearchDialog(eventFilter, eventFilter -> {
            this.eventFilter = eventFilter;
            LoadList();
        });
        exampleDialog.show(getChildFragmentManager(), "Event_filter");
    }

}