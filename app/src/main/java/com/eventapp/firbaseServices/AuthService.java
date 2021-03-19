package com.eventapp.firbaseServices;

import android.app.Activity;
import android.widget.Toast;

import com.eventapp.helpers.Apm;
import com.eventapp.models.Event;
import com.eventapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;

public class AuthService {
    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;
    private final FirebaseDatabase firebaseDatabase;
    private final Activity activity;
    private final Apm apm;
    private IAuthService authService;


    public AuthService(Activity activity, Apm apm) {
        this.activity = activity;
        this.apm = apm;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
    }


    public AuthService(Activity activity, Apm apm, IAuthService authService) {
        this.activity = activity;
        this.apm = apm;
        this.authService = authService;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
    }


    public DatabaseReference getRef() {
        return databaseReference;
    }

    public String getMyKey() {
        return firebaseAuth.getUid();
    }

    public void Register(String name, String surName, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(activity, "Error." + task.getException(), Toast.LENGTH_SHORT).show();
                        authService.AuthResponse(AuthResponse.Register, false,null);
                    } else {

                        User user = new User(name, surName, email, password);
                        // save user to database
                        databaseReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid())
                                .setValue(user).addOnCompleteListener(task1 -> {
                            if (task.isSuccessful()) {
                                apm.postUser(user);
                                authService.AuthResponse(AuthResponse.Register, true,null);
                            } else {
                                Toast.makeText(activity, "User Info error: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                authService.AuthResponse(AuthResponse.Register, false,null);
                            }

                        });

                    }

                });

    }

    public void Login(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(activity, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_LONG).show();
                authService.AuthResponse(AuthResponse.Login, false,null);
            } else {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(firebaseAuth.getUid()));
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User userModel = dataSnapshot.getValue(User.class);
                        apm.postUser(userModel);
                        authService.AuthResponse(AuthResponse.Login, true,null);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(activity, "" + databaseError.getCode(), Toast.LENGTH_SHORT).show();
                        authService.AuthResponse(AuthResponse.Login, false,null);
                    }
                });

            }
        });

    }

    public void getUser(String key) {
        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User model = dataSnapshot.getValue(User.class);
                authService.AuthResponse(AuthService.AuthResponse.GetUser, true, model);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                authService.AuthResponse(AuthService.AuthResponse.GetUser, false, null);
            }
        });
    }

    public void updateUser(String key, User user) {
        databaseReference.child(key).setValue(user);
        authService.AuthResponse(AuthResponse.UpdateUser, false,null);
    }


    public enum AuthResponse {
        Register,
        Login,
        GetUser,
        UpdateUser
    }

    public interface IAuthService {
        void AuthResponse(AuthResponse authResponse, boolean success, User user);
    }
}
