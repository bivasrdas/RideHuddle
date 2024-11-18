package com.example.ridehuddle;
import static com.example.ridehuddle.BuildConfig.MAPS_API_KEY;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.Manifest;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.internal.PolylineEncoding;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.ridehuddle.Routes;

// Implement OnMapReadyCallback.
public class DisplayMap extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
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



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest.Builder(500)
                .setMinUpdateIntervalMillis(50)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        destinationLocationEditText = findViewById(R.id.destination_location);
        destinationLocationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DisplayMap.this, PlacePickerActivity.class);
                intent.putExtra("requestCode", "destinationLocation");
                placePickerLauncher.launch(intent);
            }
        });
        floatingNavigationButton = findViewById(R.id.floatingNavigationButton);
        floatingMyLocationButton = findViewById(R.id.floatingMyLocationButton);
        startNavigation();
        myLocationButtonAction();
    }
    private void startNavigation()
    {
        floatingNavigationButton.setOnClickListener(v -> {
            Intent intent = new Intent(DisplayMap.this, NavigationActivity.class);
            intent.putExtra("destinationLocation",this.placeId);
            startActivity(intent);
        });
    }
    private void myLocationButtonAction()
    {
        floatingMyLocationButton.setOnClickListener(v -> {
            zoomToUserLocation();
        });
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
                        if (requestCode.equals("startLocation")) {
                            startLocation = placeLatLng;
                            Log.d(TAG, "Set place for start location");
                        } else if (requestCode.equals("destinationLocation")) {
                            destinationLocationEditText.setText(place_name);
                            destinationLocation = placeLatLng;
                            this.placeId = placeIdValue;
                            if (this.placeId != null)
                            {
                                routes.getRoute(this.startLocation,this.destinationLocation);
                            }
                            Log.d(TAG, "Set place for destination location "+ placeIdValue + " " + placeId);
                        }
                    }
                } else {
                    Log.e(TAG, "No predictions found or activity canceled");
                }
            });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    // Get a handle to the GoogleMap object and display marker.
    @Override
    public void onMapReady(@NonNull GoogleMap vgoogleMap) {
        googleMap = vgoogleMap;
//        googleMap.setTrafficEnabled(true);
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night));
        enableUserLocation();
        routes = new Routes(googleMap,DisplayMap.this);
    }

    LocationCallback locationCallback =  new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
//            zoomToLocation(Objects.requireNonNull(locationResult.getLastLocation()));
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
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

    private void enableLocationPermissions() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String caller = stackTraceElements[3].getMethodName();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            Log.d("Caller","Called From : "+ caller);
            try {
                this.getClass().getMethod(caller).invoke(this);
            }
            catch (NoSuchMethodException e) {
                Log.e("Error","No such method: "+ caller,e);
            }
            catch (Exception e)
            {
                Log.e("Error","Exception: ",e);
            }
        }
    }

    private void enableUserLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enableLocationPermissions();
            return;
        }
        googleMap.setMyLocationEnabled(true);
        zoomToUserLocation();
        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            locationButton.setVisibility(View.INVISIBLE);
        }
    }

    private void zoomToUserLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
            return;
        }
        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(location -> {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            startLocation = location.getLatitude()+","+location.getLongitude();
        });
    }

    private void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());

    }

    private void stopLocationUpdates(){
        fusedLocationClient.removeLocationUpdates(locationCallback);

    }

}

