package com.eventapp.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eventapp.R;
import com.eventapp.adapters.ParticipantsAdapter;
import com.eventapp.firbaseServices.AuthService;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.helpers.Apm;
import com.eventapp.models.Event;
import com.eventapp.models.EventFilter;
import com.eventapp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class ParticipantsDialog extends DialogFragment implements EventService.IEventService, AuthService.IAuthService {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private final String key;
    private EventService eventService;
    private AuthService authService;
    private ArrayList<User> users;
    private Activity activity;

    public ParticipantsDialog(String key) {
        this.key = key;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
        activity = getActivity();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_participants, container, false);
        ButterKnife.bind(this, view);
        eventService = new EventService(this);
        authService = new AuthService(activity, new Apm(activity), this);


        eventService.getEvent(key);
        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Participants");
    }

    @Override
    public void EventResponse(EventService.EventResponse response, boolean success, Event event) {
        if (response.equals(EventService.EventResponse.GetEvent) && success) {
            initList(event);

        }
    }

    private void initList(Event event) {
        users = new ArrayList<>();
        int i = 0;
        if(event.getParticipants()==null)
            return;
        for (String userKey : event.getParticipants()) {
            i++;
            int finalI = i;
            authService.getRef().child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User model = dataSnapshot.getValue(User.class);
                    users.add(model);
                    if (finalI == event.getParticipants().size())
                        showList();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void showList() {
        ParticipantsAdapter mAdapter = new ParticipantsAdapter(activity, users);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void AuthResponse(AuthService.AuthResponse response, boolean success, User user) {

    }

}
