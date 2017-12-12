package com.arm07.android.bbva.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arm07.android.bbva.R;
import com.arm07.android.bbva.model.Location;
import com.arm07.android.bbva.utility.LocationListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LocationListActivity extends AppCompatActivity {

    private ListView listView;
    private ProgressDialog progressDialog;
    ArrayList<Location> locationList;
    String urlString;

    double lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        formUrlString();
        listView = findViewById(R.id.lv);
        locationList = new ArrayList<>();
        loadListView();

        //new GetLocationList().execute();
    }

    public void formUrlString() {
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if(bundle!=null){
            lat=bundle.getDouble("latitude");
            lng=bundle.getDouble("longitude");
        }
        String latitude= Double.toString(lat);
        String longitude= Double.toString(lng);

        //urlString = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=BBVA+Compass&location=41.9048720,-88.3359800&radius=10000&key=AIzaSyDnsFg6pPAqCQl0Jw5dAnPgh8geqx4EkHI";
        urlString = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=BBVA+Compass&location="+latitude+","+longitude+"&radius=10000&key=AIzaSyCii41i77gwaslCjKryxqJnC5bQRkwYweE";

    }

   public void loadListView(){

        StringRequest sr = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("MYTEST_RESPONSE",response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject item = results.getJSONObject(i);
                        JSONObject geometry=item.getJSONObject("geometry");
                        JSONObject location=geometry.getJSONObject("location");
                        double latitude = location.getDouble("lat");
                        double longitude = location.getDouble("lng");

                        String name=item.getString("name");
                        String id=item.getString("id");
                        Location currentLocation = new Location(id,name,latitude,longitude);
                        locationList.add(currentLocation);
                    }

                    ListAdapter adapter = new LocationListAdapter(LocationListActivity.this, R.layout.list_item_view, locationList);
                    listView.setAdapter(adapter);

                    Log.i("MYTEST_RESPONSE_Finish",locationList.get(0).getId()+locationList.get(0).getName()
                            +locationList.get(0).getLatitude());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(LocationListActivity.this);
        requestQueue.add(sr);

    }
    /*public class GetLocationList extends AsyncTask {
        String response;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LocationListActivity.this);
            progressDialog.setMessage("Loading data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            StringRequest sr = new StringRequest(Request.Method.POST, urlString, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.i("MYTEST_RESPONSE",response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray results = jsonObject.getJSONArray("results");
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject item = results.getJSONObject(i);
                            JSONObject geometry=item.getJSONObject("geometry");
                            JSONObject location=geometry.getJSONObject("location");
                            double latitude = location.getDouble("lat");
                            double longitude = location.getDouble("lng");

                            String name=item.getString("name");
                            String id=item.getString("id");
                            Location currentLocation = new Location(id,name,latitude,longitude);
                            locationList.add(currentLocation);
                        }

                        ListAdapter adapter = new LocationListAdapter(LocationListActivity.this, R.layout.list_item_view, locationList);
                        listView.setAdapter(adapter);

                        Log.i("MYTEST_RESPONSE_Finish",locationList.get(0).getId()+locationList.get(0).getName()
                                +locationList.get(0).getLatitude());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(LocationListActivity.this);
            requestQueue.add(sr);
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            //      tvList.setText(response);
            //    if(progressDialog.isShowing())progressDialog.dismiss();
            progressDialog.dismiss();
            progressDialog = null;

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                   *//* Location location = locationList.get(i);
                    //Toast.makeText(LocationListActivity.this, "you selected location "+latitude+", "+longitude, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LocationListActivity.this, MapsActivity.class);
                    intent.putExtra("location", location);
                    startActivity(intent);*//*
                }
            });
        }
    }
*/
}