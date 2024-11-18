package com.example.ridehuddle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.LocationRestriction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.gms.common.api.Status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.ridehuddle.BuildConfig.MAPS_API_KEY;

public class PlacePickerActivity extends AppCompatActivity {

    private static final String TAG = "PlacePickerActivity";
    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);

        // Initialize the Places SDK
        initializePlacesApi(this);

        // Setup the Autocomplete Fragment
        setupAutocompleteFragment();

        // Fetch autocomplete predictions
//        fetchAutocompletePredictions();
    }

    /**
     * Initializes the Places API.
     */
    private void initializePlacesApi(Context context) {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(context, MAPS_API_KEY);
        }
        placesClient = Places.createClient(context);
    }

    /**
     * Sets up the AutocompleteSupportFragment and its listener.
     */
    private void setupAutocompleteFragment() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        }

        // Set up a PlaceSelectionListener to handle the response.
        if (autocompleteFragment != null) {
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // TODO: Get info about the selected place.
                    Log.d(TAG, "Place: Find" + place.getName() + ", " + place.getId());
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("place_name", place.getDisplayName());
                    if (place.getLatLng() != null) {
                        resultIntent.putExtra("place_lat", place.getLatLng().latitude);
                        resultIntent.putExtra("place_lng", place.getLatLng().longitude);
                        resultIntent.putExtra("place_Latlng",place.getLatLng().latitude + "," + place.getLatLng().longitude);
                        resultIntent.putExtra("place_id", place.getId());
                    }
                    String requestCode = getIntent().getStringExtra("requestCode");
                    if (requestCode != null) {
                        resultIntent.putExtra("requestCode", requestCode);
                    }
                    // Set the result and finish the activity.
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }


                @Override
                public void onError(@NonNull Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }
    }
}
