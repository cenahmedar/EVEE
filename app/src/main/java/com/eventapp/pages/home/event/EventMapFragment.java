package com.eventapp.pages.home.event;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eventapp.BaseFragment;
import com.eventapp.R;
import com.eventapp.firbaseServices.EventService;
import com.eventapp.helpers.BundleManager;
import com.eventapp.helpers.CurrentLocation;
import com.eventapp.helpers.Functions;
import com.eventapp.models.Event;
import com.eventapp.models.FirebaseModel;
import com.eventapp.pages.home.HomePageActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;
@SuppressLint("NonConstantResourceId")
public class EventMapFragment extends BaseFragment implements EventService.IEventService {

    private GoogleMap mMap;
  /*  private double currentLatitude;
    private double currentLongitude;*/

    private EventService eventService;
    private ArrayList<FirebaseModel<Event>> events;
    private CurrentLocation currentLocation;
    private Location myLocation;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private final OnMapReadyCallback callback = googleMap -> {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(marker -> {
            try {
                int position = Integer.parseInt(marker.getSnippet());
                bundleManager = new BundleManager();
                bundleManager.addSerializable(BundleManager.SELECTED_EVENT_KEY, events.get(position).getKey());
                activity.runOnUiThread(() -> ((HomePageActivity) activity).loadFragment(new EventDetailFragment(), true, false, bundleManager.getBundle()));
            } catch (Exception ignored) {

            }
        });
        currentLocation.getLocation();
        //goToMyLocation(location.getLatitude(), location.getLongitude());
    };


    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_map, parent, false);
        ButterKnife.bind(this, rootView);
        currentLocation = new CurrentLocation(activity, location -> {
            myLocation = location;
            goToMyLocation(location.getLatitude(), location.getLongitude());
        });
        setToolbar();
        //getCurrentLocation();
        eventService = new EventService(this);
        getEvents();
        return rootView;
    }

    private void setToolbar() {
        toolbar.setNavigationOnClickListener(v -> activity.onBackPressed());
        toolbar.setTitle("Smile :)");
    }

    private void getEvents() {
        Date currentTime = Calendar.getInstance().getTime();
        events = new ArrayList<>();
        eventService.getRef().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot ds : task.getResult().getChildren()) {
                    if (Long.parseLong(Objects.requireNonNull(ds.getValue(Event.class)).getDateTime()) >= Long.parseLong(Functions.dateTimeToInt(currentTime))) {
                        PinEvent(Objects.requireNonNull(ds.getValue(Event.class)), events.size());
                        events.add(new FirebaseModel<Event>(ds.getKey(), ds.getValue(Event.class)));
                    }
                }

            } else
                Toast.makeText(activity, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
        });
    }

    private void PinEvent(Event event, int position) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(event.getAddress().getLatitude(), event.getAddress().getLongitude()));
        markerOptions.title(event.getName());
        mMap.addMarker(markerOptions).setSnippet(position + "");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

/*    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        if (isPermisionAccess()) {
            LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location netLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (gpsLocation != null) {
                    currentLatitude = gpsLocation.getLatitude();
                    currentLongitude = gpsLocation.getLongitude();
                } else if (netLocation != null) {
                    currentLatitude = netLocation.getLatitude();
                    currentLongitude = netLocation.getLongitude();

                }
            }
        }

    }*/

    private void goToMyLocation(double currentLatitude, double currentLongitude) {
        LatLng myLocation = new LatLng(currentLatitude, currentLongitude);
        mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        //zoom to position with level 15
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 8);
        mMap.animateCamera(cameraUpdate);
    }

    private boolean isPermisionAccess() {
        return (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void EventResponse(EventService.EventResponse eventResponse, boolean success, Event event) {

    }


}