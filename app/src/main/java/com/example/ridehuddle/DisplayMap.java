package com.example.ridehuddle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ridehuddle.models.Group;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

// Implement OnMapReadyCallback.
public class DisplayMap extends AppCompatActivity implements OnMapReadyCallback {
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
                group.removeUserFromGroup();
            }
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            userLocations = new UserLocations(this,group);
            mapView = mapFragment.getView();
            mapFragment.getMapAsync(this);

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
                Intent navivationIntent = new Intent(DisplayMap.this, NavigationActivity.class);
                navivationIntent.putExtra("destinationLocation",this.placeId);
                navivationIntent.putExtra("group",group);
                startActivity(navivationIntent);
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
            userLocations.addUsersMarker(group.getUserIds(),googleMap);
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while getting map",e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userLocations.startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        userLocations.stopLocationUpdates();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

