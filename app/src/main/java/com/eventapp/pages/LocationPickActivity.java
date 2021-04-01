package com.eventapp.pages;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eventapp.R;
import com.eventapp.models.AddressModel;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

@SuppressLint("NonConstantResourceId")
public class LocationPickActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location myLocation;


    @BindView(R.id.txAddress)
    TextView txAddress;

    private AddressModel addressModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_pick);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        ButterKnife.bind(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(latLng -> {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Event");
            mMap.clear();
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.addMarker(markerOptions);

            getAddress(latLng.latitude, latLng.longitude);
        });
        getCurrentLocation();
    }

/*    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        if (isPermissionAccess()) {
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location netLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (gpsLocation != null) {
                    currentlatitude = gpsLocation.getLatitude();
                    currentlongitude = gpsLocation.getLongitude();
                } else if (netLocation != null) {
                    currentlatitude = netLocation.getLatitude();
                    currentlongitude = netLocation.getLongitude();

                }
            }
        }

    }*/

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        if (isPermissionAccess()) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (myLocation == null)
                myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (myLocation == null)
                myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (myLocation == null) { //If you need a real-time position, you should request updates even if the first location is not null
                //You don't need to use all three of these, check this answer for a complete explanation: https://stackoverflow.com/questions/6775257/android-location-providers-gps-or-network-provider

                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.myLooper());
                locationManager.requestSingleUpdate(LocationManager.PASSIVE_PROVIDER, this, Looper.myLooper());
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper()); //This consumes a lot of battery

                //10 * 1000 is the delay (in millis) between two positions update
                //10F is the minimum distance (in meters) for which you'll have update
            } else
                goToMyLocation(myLocation.getLatitude(), myLocation.getLongitude());
        }

    }

    private boolean isPermissionAccess() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @SuppressLint("SetTextI18n")
    private void getAddress(double latitude, double longitude) {
        try {
            Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                txAddress.setText("Waiting for Location");
                addressModel = null;
            } else {
                addresses.size();

                String address = addresses.get(0).getAddressLine(0);
                String state = addresses.get(0).getAdminArea();
                String countryCode = addresses.get(0).getCountryCode();

                if (countryCode.equals("TR")) {
                    txAddress.setText(address);
                    addressModel = new AddressModel(address, state, latitude, longitude);
                } else {
                    txAddress.setText("Select Address in Turkey");
                    addressModel = null;
                }

            }
        } catch (Exception e) {
            txAddress.setText(e.getMessage());
            addressModel = null;
            e.printStackTrace();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.btn_close, R.id.floatingActionButton, R.id.btnSave})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                finish();
                break;
            case R.id.floatingActionButton:
                if (myLocation != null)
                    goToMyLocation(myLocation.getLatitude(), myLocation.getLongitude());
                break;
            case R.id.btnSave:
                UpdateLocation();
                break;

        }
    }


    private void goToMyLocation(double currentlatitude, double currentlongitude) {
        mMap.clear();
        LatLng myLocation = new LatLng(currentlatitude, currentlongitude);
        mMap.addMarker(new MarkerOptions().position(new LatLng(currentlatitude, currentlongitude)).title("Current"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        //zoom to position with level 15
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLocation, 15);
        mMap.animateCamera(cameraUpdate);
        addressModel = null;
        getAddress(currentlatitude, currentlongitude);
    }


    private void UpdateLocation() {
        if (addressModel == null) {
            Toast.makeText(this, "Please Pick Valid Location", Toast.LENGTH_SHORT).show();
            return;
        }


        Intent returnIntent = new Intent();
        returnIntent.putExtra("ADDRESS_MODEL", addressModel);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        myLocation = location;
        goToMyLocation(myLocation.getLatitude(), myLocation.getLongitude());
    }
}