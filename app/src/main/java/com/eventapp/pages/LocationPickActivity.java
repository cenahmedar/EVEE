package com.eventapp.pages;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.opensooq.supernova.gligar.GligarPicker;

import java.util.List;
import java.util.Locale;

public class LocationPickActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private double currentlatitude;
    private double currentlongitude;
    private Marker marker;


    @BindView(R.id.txAddress)
    TextView txAddress;

    private AddressModel addressModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_pick);

        getCurrentLocation();
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

        goToMyLocation();
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        if (isPermisionAccess()) {
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

    }

    private boolean isPermisionAccess() {
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
                goToMyLocation();
                break;
            case R.id.btnSave:
                UpdateLocation();
                break;

        }
    }


    private void goToMyLocation() {
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

}