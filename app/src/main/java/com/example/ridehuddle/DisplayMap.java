package com.example.ridehuddle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ridehuddle.models.Group;
import com.example.ridehuddle.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Implement OnMapReadyCallback.
public class DisplayMap extends AppCompatActivity implements OnMapReadyCallback {
    FusedLocationProviderClient fusedLocationClient;

    LocationRequest locationRequest;

    private GoogleMap googleMap;

    View mapView;
    private static final String TAG = "DisplayMapActivity";
    EditText destinationLocationEditText;

    String startLocation;
    String destinationLocation;
    FloatingActionButton floatingMyLocationButton;
    FloatingActionButton floatingNavigationButton;
    String placeId;

    Routes routes;

    UserLocations userLocations;

    private Map<String,Marker> userMarker;
    Group group;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_map);
            group = (Group) getIntent().getSerializableExtra("group");
            if(group != null)
            {
                group.removeUserfromGroup();
            }
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            userLocations = new UserLocations(this);
            userMarker = new HashMap<>();
            mapView = mapFragment.getView();
            mapFragment.getMapAsync(this);
            // Initialize the UserLocations object


            // Initialize the FusedLocationProviderClient
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            locationRequest = new LocationRequest.Builder(500)
                    .setMinUpdateIntervalMillis(50)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build();

            // Initialize the EditText and FloatingActionButton
            destinationLocationEditText = findViewById(R.id.destination_location);
            floatingNavigationButton = findViewById(R.id.floatingNavigationButton);
            floatingMyLocationButton = findViewById(R.id.floatingMyLocationButton);


            // Set a click listener for the EditText to open the PlacePickerActivity
            destinationLocationEditText.setOnClickListener(v -> {
                Intent intent = new Intent(DisplayMap.this, PlacePickerActivity.class);
                intent.putExtra("requestCode", "destinationLocation");
                placePickerLauncher.launch(intent);
            });

            // Set a click listener for the FloatingActionButton to start navigation
            floatingNavigationButton.setOnClickListener(v -> {
                Intent intent = new Intent(DisplayMap.this, NavigationActivity.class);
                intent.putExtra("destinationLocation",this.placeId);
                startActivity(intent);
            });

            // Set a click listener for the FloatingActionButton to zoom to user's location
            floatingMyLocationButton.setOnClickListener(v -> {
                userLocations.zoomToUserLocation(googleMap);
            });
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while creating display map",e);
        }

    }

    private final ActivityResultLauncher<Intent> placePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    assert extras != null;
                    String place_name = extras.getString("place_name");
                    String requestCode = extras.getString("requestCode");
                    String placeLatLng = extras.getString("place_Latlng");
                    String placeIdValue = extras.getString("place_id");
                    if (place_name != null && requestCode != null) {
                        if (requestCode.equals("destinationLocation")) {
                            destinationLocationEditText.setText(place_name);
                            destinationLocation = placeLatLng;
                            this.placeId = placeIdValue;
                            this.startLocation = userLocations.getUserLocation();
                            if (this.placeId != null && this.startLocation != null)
                            {
                                routes.getRoute(this.startLocation,this.destinationLocation);
                            }
                            else {
                                Toast.makeText(this,"Enter both Locations",Toast.LENGTH_SHORT).show();
                            }
                            Log.d(TAG, "Set place for destination location "+ placeIdValue + " " + placeId);
                        }
                    }
                } else {
                    Log.e(TAG, "No predictions found or activity canceled");
                }
            });

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(@NonNull GoogleMap vgoogleMap) {
        try {
            googleMap = vgoogleMap;
//        googleMap.setTrafficEnabled(true);
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night));
            routes = new Routes(googleMap,DisplayMap.this);
            userLocations.enableUserLocation(googleMap,mapView);
            userLocations.zoomToUserLocation(googleMap);
            addUsersMarker(group.getUserIds());
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while getting map",e);
        }
    }

    LocationCallback locationCallback =  new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            updateUsersMarker(locationResult.getLastLocation());
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startLocationUpdates(){
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                userLocations.enableLocationPermissions();
                return;
            }
            fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while starting location updates",e);
        }
    }

    private void stopLocationUpdates(){
        try {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while stopping location updates",e);
        }
    }

    private void addUsersMarker(List<String> userIds) {
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
                                    userMarker.put(user.getUserId(),marker);
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
    private void updateUsersMarker(Location lastLocation) {
        try {
            db.collection("user").document(MyApp.getInstance().getUserId()).update("locations",new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude())).addOnSuccessListener(
                    unused -> Log.d(TAG,"User location updated")
            ).addOnFailureListener(
                    e -> Log.e(TAG,"Exception while updating users location",e)
            );
            db.collection("user")
                    .whereIn("userId", group.getUserIds())
                    .get().addOnSuccessListener(
                            queryDocumentSnapshots ->{
                                for(QueryDocumentSnapshot queryDocumentSnapshot: queryDocumentSnapshots)
                                {
                                    User user = queryDocumentSnapshot.toObject(User.class);
                                    LatLng latLng = new LatLng(user.getLocations().getLatitude(),user.getLocations().getLongitude());
                                    Marker marker = userMarker.get(user.getUserId());
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

