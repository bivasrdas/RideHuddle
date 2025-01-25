package com.example.ridehuddle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ridehuddle.models.Group;
import com.example.ridehuddle.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserLocations extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "UserLocations";

    Context context;

    Activity activity;

    String startLocation;
    LatLng startLocationLatLng;
    LocationRequest locationRequest;
    FirebaseFirestore db;
    Map<String, Marker> userMarkers;
    Group group;

    public UserLocations(Context context, Group group) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.activity = (Activity) context;
        this.startLocation = "";
        this.startLocationLatLng = null;
        this.locationRequest = new LocationRequest.Builder(500)
                .setMinUpdateIntervalMillis(50)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build();
        this.db = FirebaseFirestore.getInstance();
        this.userMarkers = new HashMap<>();
        this.group = group;
    }
    public UserLocations(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.activity = (Activity) context;
        this.startLocation = "";
        this.startLocationLatLng = null;
        this.locationRequest = new LocationRequest.Builder(500)
                .setMinUpdateIntervalMillis(50)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build();
        db = FirebaseFirestore.getInstance();
        userMarkers = new HashMap<>();
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
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
    public LatLng StringLatLng(String location)
    {
        try {
            String[] latLng = location.split(",");
            return new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while converting string to latlng",e);
            return null;
        }
    }
    public void startLocationUpdates(){
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                this.enableLocationPermissions();
                return;
            }
            fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while starting location updates",e);
        }
    }

    public void stopLocationUpdates(){
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while stopping location updates",e);
        }
    }
    LocationCallback locationCallback =  new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            updateUsersMarker(locationResult.getLastLocation(),group.getUserIds());
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
        }
    };

    protected void addUsersMarker(List<String> userIds, GoogleMap googleMap) {
        try {
            db.collection("user")
                    .whereIn("userId", userIds)
                    .get().addOnSuccessListener(
                            queryDocumentSnapshots ->{
                                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots)
                                {
                                    User user = queryDocumentSnapshot.toObject(User.class);
                                    LatLng latLng = new LatLng(user.getLocations().getLatitude(),user.getLocations().getLongitude());
                                    Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title(user.getUserName()));
                                    userMarkers.put(user.getUserId(),marker);
                                }
                            }
                    ).addOnFailureListener(
                            e -> Log.e(TAG,"Exception while adding users marker",e)
                    );
        }
        catch (Exception e) {
            Log.e(TAG, "Exception while adding users marker", e);
        }
    }
    protected void updateUsersMarker(Location lastLocation,List<String> userIds) {
        try {
            db.collection("user").document(MyApp.getInstance().getUserId()).update("locations",new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude())).addOnSuccessListener(
                    unused -> Log.d(TAG,"User location updated")
            ).addOnFailureListener(
                    e -> Log.e(TAG,"Exception while updating users location",e)
            );
            db.collection("user")
                    .whereIn("userId", userIds)
                    .get().addOnSuccessListener(
                            queryDocumentSnapshots ->{
                                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots)
                                {
                                    User user = queryDocumentSnapshot.toObject(User.class);
                                    LatLng latLng = new LatLng(user.getLocations().getLatitude(),user.getLocations().getLongitude());
                                    Marker marker = userMarkers.get(user.getUserId());
                                    if(marker != null)
                                    {
                                        marker.setPosition(latLng);
                                    }
                                }
                            }
                    );
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while updating users location",e);
        }
    }
}
