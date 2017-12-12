package com.arm07.android.bbva.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arm07.android.bbva.R;
import com.arm07.android.bbva.model.Location;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mGoogleMap; // Might be null if Google Play services APK is not available.

    ArrayList<Location> locationList;
    String urlString;
    double lat, lng;
    private GoogleApiClient mGoogleApiClient;

    double currentLatitude, currLongitude;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        formUrlString();
        locationList = new ArrayList<>();
        loadMapsView();
        initMap();

    }

    private void formUrlString() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            lat = bundle.getDouble("latitude");
            lng = bundle.getDouble("longitude");
        }
        String latitude = Double.toString(lat);
        String longitude = Double.toString(lng);

        //urlString = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=BBVA+Compass&location=41.9048720,-88.3359800&radius=10000&key=AIzaSyDnsFg6pPAqCQl0Jw5dAnPgh8geqx4EkHI";
        urlString = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=BBVA+Compass&location=" + latitude + "," + longitude + "&radius=10000&key=AIzaSyCii41i77gwaslCjKryxqJnC5bQRkwYweE";

    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMaps2);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(MapsActivity.this, TAG + "On Map Ready", Toast.LENGTH_SHORT).show();
        mGoogleMap = googleMap;

        if (checkPermissions()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mGoogleMap.setMyLocationEnabled(true);
        }

    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void addMarkersToMap() {



    }

    private void loadMapsView() {
        StringRequest sr = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(MapsActivity.this, TAG +"before loading Json", Toast.LENGTH_SHORT).show();

                Log.i("MYTEST_RESPONSE", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject item = results.getJSONObject(i);
                        JSONObject geometry = item.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double latitude = location.getDouble("lat");
                        double longitude = location.getDouble("lng");

                        String name = item.getString("name");
                        String id = item.getString("id");
                        Location currentLocation = new Location(id, name, latitude, longitude);
                        locationList.add(currentLocation);
                    }

                   /* Toast.makeText(MapsActivity.this, TAG +"after loading Json"+locationList.get(0).getId() + locationList.get(0).getName()
                            + locationList.get(0).getLatitude(), Toast.LENGTH_SHORT).show();
*/
                    Log.i("TESTMAPS_Res_Finish", locationList.get(0).getId() + locationList.get(0).getName()
                            + locationList.get(0).getLatitude());

                    if(locationList.size()==0){
                        Toast.makeText(MapsActivity.this, TAG + "On Map Ready- markers not added", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        for (int i = 0; i < locationList.size(); i++) {
                            currentLatitude = locationList.get(i).getLatitude();
                            currLongitude = locationList.get(i).getLongitude();
                            LatLng latLng = new LatLng(currentLatitude, currLongitude);
                            String title = locationList.get(i).getName();
                            MarkerOptions options = new MarkerOptions()
                                    .position(latLng)
                                    .title(title);
                            mGoogleMap.addMarker(options);

                        }
                        Toast.makeText(MapsActivity.this, TAG + "On Map Ready- markers added", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        requestQueue.add(sr);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initMap();
        //      setUpMapIfNeeded();
        // mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
