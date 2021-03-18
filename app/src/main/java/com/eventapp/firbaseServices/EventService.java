package com.eventapp.firbaseServices;

import com.eventapp.models.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

public class EventService {
    private final DatabaseReference databaseReference;
    private final FirebaseDatabase firebaseDatabase;
    private IEventService iEventService;
    private final FirebaseAuth firebaseAuth;

    public EventService() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Events");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public EventService(IEventService iEventService) {
        this.iEventService = iEventService;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Events");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public DatabaseReference getRef() {
        return databaseReference;
    }

    public void insertEvent(Event event) {
        event.setUserKey(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        databaseReference.push().setValue(event);
        iEventService.EventResponse(EventResponse.InsertEvent, true, null);
    }

    public void updateEvent(String key, Event event) {
        databaseReference.child(key).setValue(event);
        iEventService.EventResponse(EventResponse.InsertEvent, true, null);
    }

    public void getEvent(String key) {
        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event model = dataSnapshot.getValue(Event.class);
                iEventService.EventResponse(EventResponse.GetEvent, true, model);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iEventService.EventResponse(EventResponse.GetEvent, false, null);
            }
        });
    }

    public void joinUnJoinEvent(String key, Event model) {
        if (model.getParticipants() == null || model.getParticipants().size() == 0) {
            model.setParticipants(new ArrayList<>());
            model.addParticipant(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        } else if (model.getParticipants().contains(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())) {
            model.removeParticipant(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        } else {
            model.addParticipant(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        }
        databaseReference.child(key).setValue(model);
        iEventService.EventResponse(EventResponse.JoinUnJoinEvent, true, null);

    }

    public void Delete(String key) {
        databaseReference.child(key).removeValue();
        iEventService.EventResponse(EventResponse.DeleteEvent, true, null);
    }


    public enum EventResponse {
        InsertEvent,
        UpdateEvent,
        GetEvent,
        JoinUnJoinEvent,
        DeleteEvent
    }

    public interface IEventService {
        void EventResponse(EventResponse eventResponse, boolean success, Event event);
    }


}
