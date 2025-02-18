package com.example.ridehuddle;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ridehuddle.models.Group;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.navigation.ListenableResultFuture;
import com.google.android.libraries.navigation.NavigationApi;
import com.google.android.libraries.navigation.NavigationApi.NavigatorListener;
import com.google.android.libraries.navigation.NavigationView;
import com.google.android.libraries.navigation.Navigator;
import com.google.android.libraries.navigation.Navigator.RouteStatus;
import com.google.android.libraries.navigation.Waypoint;

public class NavigationActivity extends AppCompatActivity {

    private static final String TAG = "NavigationActivity";

    private NavigationView navigationView;
    private Navigator navigator;

    private GoogleMap googleMap;

    UserLocations userLocations;
    Group group;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_view);
        group = (Group) getIntent().getSerializableExtra("group");
        // Initialize the NavigationView
        userLocations = new UserLocations(this,group);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.onCreate(savedInstanceState);
        String destinationPlaceId = getIntent().getStringExtra("destinationLocation");

        // Initialize the Navigation API
        initializeNavigationApi(destinationPlaceId);
    }

    private void initializeNavigationApi(String placeId) {
        try {
            NavigationApi.getNavigator(this, new NavigatorListener() {
                @Override
                public void onNavigatorReady(@NonNull Navigator mnavigator) {
                    navigator = mnavigator;
                    navigationView.setNavigationUiEnabled(true);
                    if (ActivityCompat.checkSelfPermission(NavigationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NavigationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        userLocations.enableLocationPermissions();
                    }
                    navigationView.getMapAsync(googleMap -> {
                        googleMap.followMyLocation(GoogleMap.CameraPerspective.TILTED);
                        userLocations.startLocationUpdates();
                        userLocations.addUsersMarker(group.getUserIds(),googleMap);
                    });
                    navigator.startGuidance();

                    Log.d(TAG, "Navigator is ready");
                    startNavigationToPlace(placeId);
                }

                @Override
                public void onError(int errorCode) {
                    Log.e(TAG, "Navigator initialization error: " + errorCode);
                    Toast.makeText(NavigationActivity.this, "Error initializing navigator", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error while initializing navigator : "+e.getMessage());
        }
    }

    private void startNavigationToPlace(String id) {
       try {
           if (navigator != null && id != null && navigationView != null) {
               Waypoint waypoint = Waypoint.builder()
                       .setPlaceIdString(id)
                       .build();

               ListenableResultFuture<RouteStatus> pendingRoute = navigator.setDestination(waypoint);
               pendingRoute.setOnResultListener(code -> {
                   if (code == RouteStatus.OK) {
                       navigator.startGuidance();
                       Toast.makeText(this, "Navigation started", Toast.LENGTH_SHORT).show();
                   } else {
                       Toast.makeText(this, "Failed to start navigation", Toast.LENGTH_SHORT).show();
                   }
               });
           }
           else
           {
               Toast.makeText(this, "Navigation not started", Toast.LENGTH_SHORT).show();
           }
       }
       catch (Waypoint.UnsupportedPlaceIdException e)
       {
           Log.e(TAG, "Error while navigation : "+e.getMessage());
       }
    }

    private void addMarkerOnMap(LatLng position, String title,GoogleMap googleMap) {
        if (googleMap != null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(position)
                    .title(title);
            googleMap.addMarker(markerOptions);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        navigationView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.onResume();
    }

    @Override
    protected void onPause() {
        navigationView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        navigationView.onStop();
        userLocations.stopLocationUpdates();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//        navigationView.onDestroy();
        super.onDestroy();
        navigator.stopGuidance();
        navigator.clearDestinations();
        navigationView.setNavigationUiEnabled(false);
    }
}
