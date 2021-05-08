package com.eventapp.firbaseServices;

import android.widget.Toast;

import com.eventapp.models.Event;
import com.eventapp.models.Rating;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;

public class RatingService {
    private final DatabaseReference databaseReference;
    private final FirebaseDatabase firebaseDatabase;
    private final FirebaseAuth firebaseAuth;
    private IRatingService iRatingService;

    public RatingService(IRatingService iRatingService) {
        this.iRatingService = iRatingService;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Rating");
        firebaseAuth = FirebaseAuth.getInstance();
    }


    public void insertRate(Rating rating) {
        rating.setUserKey(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
        databaseReference.child(rating.getUserKey()).child(rating.getEventKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(rating.getUserKey()).exists()) {
                    //remove old value
                    databaseReference.child(rating.getUserKey()).removeValue();
                }
                databaseReference.child(rating.getUserKey()).setValue(rating);
                iRatingService.RatingResponse(RatingResponse.InsertRate, true, 0);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getEventRate(String key) {
        Query productRating = databaseReference.orderByChild("eventKey").equalTo(key);

        productRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0, sum = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    if (item != null) {
                        sum += Integer.parseInt(item.getRateValue());
                        count++;
                    }

                }
                if (count != 0) {
                    float average = sum / count;
                    iRatingService.RatingResponse(RatingResponse.EventRate, true, average);
                } else
                    iRatingService.RatingResponse(RatingResponse.EventRate, true, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getForEvent(String key) {
        Query productRating = databaseReference.orderByChild("eventKey").equalTo(key);

        productRating.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Rating> ratings = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Rating item = postSnapshot.getValue(Rating.class);
                    ratings.add(item);
                }
                iRatingService.ListResponse(ratings);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public enum RatingResponse {
        InsertRate,
        EventRate
    }

    public interface IRatingService {
        void RatingResponse(RatingResponse eventResponse, boolean success, float rate);
        void ListResponse(ArrayList<Rating> ratings);
    }

}
