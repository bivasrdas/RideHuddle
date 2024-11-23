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
        try {
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
                        Log.d(TAG,jsonResponse);
                        mainHandler.post(() -> handleRouteResponse(jsonResponse));
                    }
                }
            });
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while fetching directions",e);
        }
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
        try {
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
        catch (Exception e)
        {
            Log.e(TAG,"Exception while drawing polylines",e);
        }
    }

    private void addTrafficSegment(JSONObject trafficSegment, List<LatLng> decodedPath) throws JSONException {
        try {
            Log.d(TAG,trafficSegment.toString());
            Log.d(TAG,decodedPath.size()+"");
            int startIndex = trafficSegment.getInt("startPolylinePointIndex");
            int endIndex = trafficSegment.getInt("endPolylinePointIndex");
            String speed = trafficSegment.getString("speed");

            PolylineOptions polylineOptions = new PolylineOptions()
                    .width(19f)
                    .geodesic(true)
                    .color(getColorForSpeed(speed));

            for (int i = startIndex; i <= endIndex && i <decodedPath.size(); i++) {
                polylineOptions.add(decodedPath.get(i));
            }

            Polyline polyline = googleMap.addPolyline(polylineOptions);
            polylineList.add(polyline);
        } catch (Exception e) {
            Log.d(TAG, "Error adding traffic segment", e);
        }
    }

    private void drawMarkers(String origin, String destination) {
        try {
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
        catch (Exception e)
        {
            Log.e(TAG,"Exception while drawing markers",e);
        }
    }

    /* =================== Marker and Polyline Clearing =================== */

    private void clearMarkers() {
        try {
            for (Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while clearing markers",e);
        }
    }

    private void clearPolylines() {
        try {
            for (Polyline polyline : polylineList) {
                polyline.remove();
            }
            polylineList.clear();
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while clearing polylines",e);
        }
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
        try
        {
            JSONObject originLatLng = directionPayload.getJSONObject("origin").getJSONObject("location").getJSONObject("latLng");
            JSONObject destinationLatLng = directionPayload.getJSONObject("destination").getJSONObject("location").getJSONObject("latLng");

            originLatLng.put("latitude", origin.split(",")[0]);
            originLatLng.put("longitude", origin.split(",")[1]);
            destinationLatLng.put("latitude", destination.split(",")[0]);
            destinationLatLng.put("longitude", destination.split(",")[1]);
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while updating route coordinates",e);
        }
    }

    private LatLng parseLatLng(String coordinate) {
        try {
            String[] latLng = coordinate.split(",");
            return new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while parsing latlng",e);
            return null;
        }
    }

    private List<LatLng> decodePolyline(String encodedPolyline) {
        try {
            List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(encodedPolyline);
            List<LatLng> latLngList = new ArrayList<>();
            for (com.google.maps.model.LatLng point : decodedPath) {
                latLngList.add(new LatLng(point.lat, point.lng));
            }
            return latLngList;
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while decoding polyline",e);
            return null;
        }
    }

    private static int getColorForSpeed(String speed) {
        try {
            switch (speed) {
                case "NORMAL": return Color.parseColor("#00B0FF");
                case "SLOW": return Color.YELLOW;
                case "TRAFFIC_JAM": return Color.RED;
                default: return Color.GRAY;
            }
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception while getting color for speed",e);
            return Color.GRAY;
        }
    }
}
