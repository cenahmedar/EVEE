package com.eventapp.pages.home.event;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.helpers.BundleManager;
import com.eventapp.models.Event;
import com.eventapp.models.User;
import com.squareup.picasso.Picasso;

@SuppressLint("NonConstantResourceId")
public class EventDetailFragment extends BaseFragment implements EventService.IEventService, AuthService.IAuthService {


    private View rootView;
    private String key;
    private EventService eventService;
    private AuthService authService;
    private Event event;

    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.tx_name)
    TextView tx_name;

    @BindView(R.id.tx_location)
    TextView tx_location;

    @BindView(R.id.tx_date)
    TextView tx_date;

    @BindView(R.id.tx_time)
    TextView tx_time;

    @BindView(R.id.tx_desc)
    TextView tx_desc;

    @BindView(R.id.tx_user_name)
    TextView tx_user_name;

    @BindView(R.id.tx_count)
    TextView tx_count;


    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event_detail, parent, false);
        ButterKnife.bind(this, rootView);

        eventService = new EventService(this);
        authService = new AuthService(activity, apm, this);
        key = (String) bundleManager.getSerializable(this, BundleManager.SELECTED_EVENT_KEY);

        init();
        return rootView;
    }

    private void init() {
        progressBarBuilder.show();
        eventService.getEvent(key);
    }


    @Override
    public void EventResponse(EventService.EventResponse response, boolean success, Event event) {
        if (response.equals(EventService.EventResponse.GetEvent) && success) {
            progressBarBuilder.hide();
            this.event = event;
            authService.getUser(event.getUserKey());

        }
    }

    @SuppressLint("SetTextI18n")
    private void setEvent() {
        tx_name.setText(event.getName());
        tx_location.setText(event.getAddress().getAddress());
        tx_date.setText(event.getDate());
        tx_time.setText(event.getTime());
        tx_desc.setText(event.getDescription());
        tx_user_name.setText(event.getUser().getName() + " " + event.getUser().getSurName());
        Picasso.get().load(event.getImage()).into(imageView);
    }

    @Override
    public void AuthResponse(AuthService.AuthResponse response, boolean success, User user) {
        if (response.equals(AuthService.AuthResponse.GetUser) && success) {
            progressBarBuilder.hide();
            authService.getUser(event.getUserKey());
            event.setUser(user);
            setEvent();
        }


    }
}