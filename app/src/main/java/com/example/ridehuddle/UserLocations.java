package com.example.ridehuddle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class UserLocations extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "UserLocations";

    Context context;

    Activity activity;

    String startLocation;
    LatLng startLocationLatLng;

    public UserLocations(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.activity = (Activity) context;
        this.startLocation = "";
        this.startLocationLatLng = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        try {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while handling request permissions result",e);
        }
    }
    protected void enableLocationPermissions() {
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            String caller = stackTraceElements[3].getMethodName();
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
                Log.d("Caller","Called From : "+ caller);
                this.getClass().getMethod(caller).invoke(this);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while enabling location permissions",e);
        }
    }

    protected void enableUserLocation(GoogleMap googleMap, View mapView){
        try {
            if (checkLocationPermission()) {
                enableLocationPermissions();
                return;
            }
            googleMap.setMyLocationEnabled(true);
            if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
                // Get the button view and make it visible(Justification : Unable to change the position and size of the button provided by default google map)
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                locationButton.setVisibility(View.INVISIBLE);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while enabling user location",e);
        }
    }

    protected void zoomToUserLocation(GoogleMap googleMap)
    {
        try {
            if (checkLocationPermission()) {
                enableLocationPermissions();
            }
            Task<Location> locationTask = fusedLocationClient.getLastLocation();
            locationTask.addOnSuccessListener(location -> {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                startLocation = location.getLatitude()+","+location.getLongitude();
            }).addOnFailureListener(location ->{
                Log.e(TAG,"Get Last Location failure",location);
            });
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while getting user location",e);
        }

    }

    protected void setUpdateUserLocation()
    {
        try {
            if (checkLocationPermission()) {
                enableLocationPermissions();
            }
            Task<Location> locationTask = fusedLocationClient.getLastLocation();
            locationTask.addOnSuccessListener(location -> {
                startLocation = location.getLatitude()+","+location.getLongitude();
                startLocationLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            }).addOnFailureListener(location ->{
                    Log.e(TAG,"Get Last Location failure",location);
            });
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while getting user location",e);
        }
    }
    protected String getUserLocation() {
        try {
            setUpdateUserLocation();
            return this.startLocation;
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while getting user location",e);
            return "";
        }
    }
    protected boolean checkLocationPermission()
    {
        try {
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while checking location permission",e);
        }
        return false;
    }

    protected void zoomToLocation(Location lastLocation, GoogleMap googleMap)
    {
        try {
            LatLng latLng = new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while zooming to location",e);
        }
    }
}
