package com.arm07.android.bbva.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.arm07.android.bbva.R;
import com.arm07.android.bbva.utility.LocationProvider;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,LocationProvider.LocationCallback {

    public static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap mGoogleMap; // Might be null if Google Play services APK is not available.

    private LocationProvider mLocationProvider;

    int idLoc = R.id.listLocations;

    double currentLatitude,currLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        initMap();
        mLocationProvider = new LocationProvider(this, this);
        mLocationProvider.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initMap();
        //      setUpMapIfNeeded();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    private void initMap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragmentMaps);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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

    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        if (location == null)
            Toast.makeText(this, "cannot get location updates", Toast.LENGTH_SHORT).show();
        else {
            currentLatitude = location.getLatitude();
            currLongitude = location.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currLongitude);

            //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
            mGoogleMap.animateCamera(cameraUpdate);

            Toast.makeText(this, "camera updated to curr location", Toast.LENGTH_SHORT).show();

            //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("I am here!");
            mGoogleMap.addMarker(options);
            /*mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));*/
        }
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (idLoc = item.getItemId()) {
            case R.id.listLocations:
                Toast.makeText(this, "Locations in List View", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,LocationListActivity.class);
                intent.putExtra("latitude",currentLatitude);
                intent.putExtra("longitude",currLongitude);
                startActivity(intent);
                return true;
            case R.id.LocationsInMap:
                Toast.makeText(this, "Locations in List View", Toast.LENGTH_SHORT).show();
                Intent intent2=new Intent(MainActivity.this,MapsActivity.class);
                intent2.putExtra("latitude",currentLatitude);
                intent2.putExtra("longitude",currLongitude);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
