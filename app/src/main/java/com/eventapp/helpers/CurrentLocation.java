package com.eventapp.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.eventapp.BuildConfig;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class CurrentLocation {

    public interface ICurrentLocation{
        void callBack(Location location);
    }

    private ProgressBarBuilder progressBarBuilder;
    private ICurrentLocation iCurrentLocation;

    private static final String TAG = "MapsActivity";

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;


    // location updates interval - 20sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 200000;
    private Location mLastLocation;
    private Activity activity;

    public final static int REQUEST_CHECK_SETTINGS = 100;

    public CurrentLocation(Activity activity,ICurrentLocation iCurrentLocation) {
        this.activity = activity;
        progressBarBuilder = new ProgressBarBuilder(activity);
        this.iCurrentLocation = iCurrentLocation;
    }
    public void getLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        checkForPermissions();
    }

    private void checkForPermissions() {
        Dexter.withContext(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // konum iznine evet derse
                        // mRequestingLocationUpdates = true;
                        checkGbs();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            // eger location iznine kesinlikle hayır derse ayarlara atar
                            //openSettings();
                            Toast.makeText(activity, "Please Open Location From Settings..", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        } else {
                            Toast.makeText(activity, "Please Accept Location Service", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        // burda konum izinleri istiyor
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    @SuppressLint("MissingPermission")
    private void checkGbs() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(activity).checkLocationSettings(builder.build());
        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
                // All location settings are satisfied. The client can initialize location
                // requests here.
                progressBarBuilder.show();
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

            } catch (ApiException exception) {
                String error = exception.getMessage();
                switch (exception.getStatusCode()) {

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                "location settings ");
                        // Toast.makeText(context, "Location settings are not satisfied." + "" + error, Toast.LENGTH_LONG).show();
                        // Toast.makeText(context, "Location settings are not satisfied. Attempting to upgrade location settings ", Toast.LENGTH_SHORT).show();
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the
                            // result in onActivityResult().
                            ResolvableApiException rae = (ResolvableApiException) exception;
                            rae.startResolutionForResult((Activity) activity, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sie) {
                            Log.i(TAG, "PendingIntent.");
                            Toast.makeText(activity, ".", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        String errorMessage = "Location settings are inadequate, and cannot be " +
                                "fixed here. Fix in Settings.";
                        Toast.makeText(activity, "Konum ayarları yetersizdir lütfen ayarlardan düzeltiniz." + "" + error, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, errorMessage);

                        // Toast.makeText((Activity) context, errorMessage, Toast.LENGTH_LONG).show();
                    default:
                        Toast.makeText(activity, "unkown error." + "" + error, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private final LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            progressBarBuilder.hide();
            for (Location location : locationResult.getLocations()) {
                if (location != null) mLastLocation = location;
            }
            if (null != mLastLocation) {
             //ok
            iCurrentLocation.callBack(mLastLocation);
            } else {
                //get last Known Location
                Toast.makeText(activity, "Konum bulunamadı", Toast.LENGTH_SHORT).show();
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        iCurrentLocation.callBack(location);
                    }
                });
            }
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            if (locationAvailability.isLocationAvailable()) {
                super.onLocationAvailability(locationAvailability);
                return;
            }
            progressBarBuilder.hide();
            Toast.makeText(activity, "locationAvailability false", Toast.LENGTH_SHORT).show();
            super.onLocationAvailability(locationAvailability);
        }
    };
}
