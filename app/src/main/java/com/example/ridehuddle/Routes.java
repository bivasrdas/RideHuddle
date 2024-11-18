//package com.example.ridehuddle;
//
//import static com.example.ridehuddle.BuildConfig.MAPS_API_KEY;
//
//import android.graphics.Color;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.Polyline;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.maps.internal.PolylineEncoding;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.List;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//
//public class Routes extends AppCompatActivity {
//
//    protected static final String TAG = "Routes";
//    OkHttpClient client;
//    private final List<Polyline> polylineList;
//    GoogleMap googleMap;
//
//    public Routes(OkHttpClient client, List<Polyline> polylineList, GoogleMap googleMap) {
//        this.client = client;
//        this.polylineList = polylineList;
//        this.googleMap = googleMap;
//    }
//
//
//    protected String getRoutePayload() {
//        try {
//            InputStreamReader inputStreamReader = new InputStreamReader(getAssets().open("directions_payload.json"));
//            BufferedReader reader = new BufferedReader(inputStreamReader);
//            StringBuilder jsonStringBuilder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                jsonStringBuilder.append(line);
//            }
//            reader.close();
//            return jsonStringBuilder.toString();
//        } catch (Exception e) {
//            Log.e(TAG, "Error reading JSON file", e);
//            return null;
//        }
//    }
//    protected void getRoute(String origin, String destination) throws JSONException {
//        try {
//            String url = "https://routes.googleapis.com/directions/v2:computeRoutes";
//            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//            JSONObject direction_payload = new JSONObject(getRoutePayload());
//            JSONObject origin_latitude = direction_payload.getJSONObject("origin").getJSONObject("location").getJSONObject("latLng");
//            JSONObject origin_longitude = direction_payload.getJSONObject("origin").getJSONObject("location").getJSONObject("latLng");
//            JSONObject destination_latitude = direction_payload.getJSONObject("destination").getJSONObject("location").getJSONObject("latLng");
//            JSONObject destination_longitude = direction_payload.getJSONObject("destination").getJSONObject("location").getJSONObject("latLng");
//            origin_latitude.put("latitude",origin.split(",")[0]);
//            origin_longitude.put("longitude",origin.split(",")[1]);
//            destination_latitude.put("latitude",destination.split(",")[0]);
//            destination_longitude.put("longitude",destination.split(",")[1]);
//            LatLng origin_lat_long = new LatLng(Double.parseDouble(origin_latitude.getString("latitude")),Double.parseDouble(origin_longitude.getString("longitude")));
//            googleMap.addMarker(new MarkerOptions().position(origin_lat_long).title("Origin"));
//            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin_lat_long,16));
//            LatLng destination_lat_long = new LatLng(Double.parseDouble(destination_latitude.getString("latitude")),Double.parseDouble(destination_longitude.getString("longitude")));
//            googleMap.addMarker(new MarkerOptions().position(destination_lat_long).title("Destination"));
//            // Create the request body
//            RequestBody body = RequestBody.create(JSON, String.valueOf(direction_payload));
//            Request request = new Request.Builder()
//                    .url(url)
//                    .addHeader("X-Goog-Api-Key", MAPS_API_KEY)
//                    .addHeader("X-Goog-FieldMask","*")
//                    .post(body)
//                    .build();
//            client.newCall(request).enqueue(new  Callback() {
//                @Override
//                public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                    Log.e("Directions", "Error fetching directions: " + e.getMessage());
//                }
//                @Override
//                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                    try {
//                        if (response.isSuccessful()) {
//                            String jsonResponse = "";
//                            if(response.body() != null)
//                            {
//                                jsonResponse = response.body().string();
//                                addPolylinesToMap(jsonResponse);
//                            }
//                        }
//                    } catch (JSONException e) {
//                        Log.e(TAG, "onResponse: Error parsing JSON response: " + e.getMessage());
//                    }
//                    catch (Exception e)
//                    {
//                        Log.e("Directions", "Error fetching directions: " + e.getMessage());
//                    }
//                }
//            });
//        }
//        catch (Exception e) {
//            Log.e("Directions", "Error fetching directions: " + e.getMessage());
//        }
//    }
//
//    private void addPolylinesToMap(String result) throws JSONException {
//        try {
//            JSONObject jsonObject = new JSONObject(result);
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        JSONArray routes = jsonObject.getJSONArray("routes");
//                        Log.d(TAG, "run: leg: " + routes.getClass());
//                        clearPolylines();
//                        for (int i = 0; i < routes.length(); i++) {
//                            JSONArray legs = routes.getJSONObject(i).getJSONArray("legs");
//                            for (int j = 0; j < legs.length(); j++) {
//                                // Get the encoded polyline
//                                String encodedPolyline = legs.getJSONObject(j).getJSONObject("polyline").getString("encodedPolyline");
//                                JSONArray traffic = legs.getJSONObject(j).getJSONObject("travelAdvisory").getJSONArray("speedReadingIntervals");
//
//                                // Decode the polyline
//                                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(encodedPolyline);
//                                List<PolylineOptions> multiPolylines = new ArrayList<>();
//
//                                // Draw segmented routes based on traffic data
//                                for (int t = 0; t < traffic.length(); t++) {
//                                    PolylineOptions polylineOptions = new PolylineOptions();
//                                    polylineOptions.width(19f).geodesic(true);
//
//                                    // Get traffic segment details
//                                    int startIndex = traffic.getJSONObject(t).getInt("startPolylinePointIndex");
//                                    int endIndex = traffic.getJSONObject(t).getInt("endPolylinePointIndex");
//                                    String speed = traffic.getJSONObject(t).getString("speed");
//                                    int color = getColorForSpeed(speed);
//                                    polylineOptions.color(color);
//
//                                    // Add points for the current segment
//                                    for (int path = startIndex; path <= endIndex; path++) {
//                                        polylineOptions.add(new LatLng(decodedPath.get(path).lat, decodedPath.get(path).lng));
//                                    }
//
//                                    // Store the polyline for future removal
//                                    Polyline polyline = googleMap.addPolyline(polylineOptions);
//                                    polylineList.add(polyline);
//                                }
//                            }
//                        }
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            });
//        } catch (Exception e) {
//            Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());
//        }
//    }
//    private void clearPolylines() {
//        for (Polyline polyline : polylineList) {
//            polyline.remove();
//        }
//        polylineList.clear();
//    }
//    private static int getColorForSpeed(String speed) {
//        try {
//            // Parse the JSON array
//            switch (speed) {
//                case "NORMAL":
//                    return Color.parseColor("#00B0FF");   // Color for NORMAL
//                case "SLOW":
//                    return Color.YELLOW; // Color for SLOW
//                case "TRAFFIC_JAM":
//                    return Color.RED;    // Color for TRAFFIC JAM
//                default:
//                    return Color.GRAY;   // Default color if speed is unknown
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return Color.GRAY; // Return GRAY if no match found
//    }
//}


package com.example.ridehuddle;

import static com.example.ridehuddle.BuildConfig.MAPS_API_KEY;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.internal.PolylineEncoding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class Routes extends AppCompatActivity {

    protected static final String TAG = "Routes";
    private final OkHttpClient client;
    private final List<Polyline> polylineList;
    private final List<Marker> markerList;
    private final GoogleMap googleMap;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Context context;

    public Routes(GoogleMap googleMap, Context context) {
        this.client = new OkHttpClient();
        this.polylineList = new ArrayList<>();
        this.markerList = new ArrayList<>();
        this.googleMap = googleMap;
        this.context = context;
    }

    /* =================== Public Methods =================== */

    public void getRoute(String origin, String destination) {
        try {
            JSONObject directionPayload = getRoutePayload();
            updateRouteCoordinates(directionPayload, origin, destination);
            clearMarkers(); // Clear previous markers
            drawMarkers(origin, destination);
            fetchRouteFromAPI(directionPayload);
        } catch (JSONException e) {
            Log.e(TAG, "Error updating route coordinates", e);
        }
    }

    /* =================== Private Methods =================== */

    private void fetchRouteFromAPI(JSONObject directionPayload) {
        String url = "https://routes.googleapis.com/directions/v2:computeRoutes";
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, directionPayload.toString());
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Goog-Api-Key", MAPS_API_KEY)
                .addHeader("X-Goog-FieldMask", "*")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "Error fetching directions: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String jsonResponse = response.body().string();
                    mainHandler.post(() -> handleRouteResponse(jsonResponse));
                }
            }
        });
    }

    private void handleRouteResponse(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray routes = jsonObject.getJSONArray("routes");
            clearPolylines();

            for (int i = 0; i < routes.length(); i++) {
                drawPolylines(routes.getJSONObject(i));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON response", e);
        }
    }

    private void drawPolylines(JSONObject route) throws JSONException {
        JSONArray legs = route.getJSONArray("legs");

        for (int j = 0; j < legs.length(); j++) {
            String encodedPolyline = legs.getJSONObject(j).getJSONObject("polyline").getString("encodedPolyline");
            List<LatLng> decodedPath = decodePolyline(encodedPolyline);

            JSONArray traffic = legs.getJSONObject(j).getJSONObject("travelAdvisory").getJSONArray("speedReadingIntervals");
            for (int t = 0; t < traffic.length(); t++) {
                addTrafficSegment(traffic.getJSONObject(t), decodedPath);
            }
        }
    }

    private void addTrafficSegment(JSONObject trafficSegment, List<LatLng> decodedPath) throws JSONException {
        int startIndex = trafficSegment.getInt("startPolylinePointIndex");
        int endIndex = trafficSegment.getInt("endPolylinePointIndex");
        String speed = trafficSegment.getString("speed");

        PolylineOptions polylineOptions = new PolylineOptions()
                .width(19f)
                .geodesic(true)
                .color(getColorForSpeed(speed));

        for (int i = startIndex; i <= endIndex; i++) {
            polylineOptions.add(decodedPath.get(i));
        }

        Polyline polyline = googleMap.addPolyline(polylineOptions);
        polylineList.add(polyline);
    }

    private void drawMarkers(String origin, String destination) {
        LatLng originLatLng = parseLatLng(origin);
        LatLng destinationLatLng = parseLatLng(destination);

        Marker originMarker = googleMap.addMarker(new MarkerOptions().position(originLatLng).title("Origin"));
        Marker destinationMarker = googleMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

        if (originMarker != null) markerList.add(originMarker);
        if (destinationMarker != null) markerList.add(destinationMarker);
        assert originMarker != null;
        originMarker.remove();
        markerList.remove(originMarker);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(originLatLng, 16));
    }

    /* =================== Marker and Polyline Clearing =================== */

    private void clearMarkers() {
        for (Marker marker : markerList) {
            marker.remove();
        }
        markerList.clear();
    }

    private void clearPolylines() {
        for (Polyline polyline : polylineList) {
            polyline.remove();
        }
        polylineList.clear();
    }

    /* =================== Utility Methods =================== */

    private JSONObject getRoutePayload() throws JSONException {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open("directions_payload.json"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonStringBuilder.append(line);
            }
            reader.close();
            return new JSONObject(jsonStringBuilder.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error reading JSON file", e);
            throw new JSONException("Failed to load JSON payload");
        }
    }

    private void updateRouteCoordinates(JSONObject directionPayload, String origin, String destination) throws JSONException {
        JSONObject originLatLng = directionPayload.getJSONObject("origin").getJSONObject("location").getJSONObject("latLng");
        JSONObject destinationLatLng = directionPayload.getJSONObject("destination").getJSONObject("location").getJSONObject("latLng");

        originLatLng.put("latitude", origin.split(",")[0]);
        originLatLng.put("longitude", origin.split(",")[1]);
        destinationLatLng.put("latitude", destination.split(",")[0]);
        destinationLatLng.put("longitude", destination.split(",")[1]);
    }

    private LatLng parseLatLng(String coordinate) {
        String[] latLng = coordinate.split(",");
        return new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
    }

    private List<LatLng> decodePolyline(String encodedPolyline) {
        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(encodedPolyline);
        List<LatLng> latLngList = new ArrayList<>();
        for (com.google.maps.model.LatLng point : decodedPath) {
            latLngList.add(new LatLng(point.lat, point.lng));
        }
        return latLngList;
    }

    private static int getColorForSpeed(String speed) {
        switch (speed) {
            case "NORMAL": return Color.parseColor("#00B0FF");
            case "SLOW": return Color.YELLOW;
            case "TRAFFIC_JAM": return Color.RED;
            default: return Color.GRAY;
        }
    }
}
