package com.example.eventgoapps.ui;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.MapsApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.databinding.ActivityEventLocationBinding;
import com.example.eventgoapps.util.DirectionJSONParser;
import com.example.eventgoapps.util.Utils;
import com.example.eventgoapps.util.ViewWeightAnimationWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.eventgoapps.R;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "EventLocationActivity";

    private GoogleMap mMap;
    ApiRequest apiRequest;
    ApiRequest mapsApiRequest;
    String idEvent;
    private static final int MY_PERMISSION_REQUEST_CODE = 732;
    public static final int UPDATE_INTERVAL = 5000;
    public static final int FASTEST_INTERVAL = 5000;
    public static final int DISPLACEMENT = 10;

    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mMapLayoutState = 0;

    private LocationRequest mLocationReqeust;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;

    ActivityEventLocationBinding binding;


    Marker mCurrentMarker;
    private Polyline directionTracking;
    boolean displayRute = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        idEvent = getIntent().getStringExtra("id_event");
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        mapsApiRequest = MapsApiRequest.getClient(Utils.mapsUrl).create(ApiRequest.class);

        loadDataEvent();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setUpLocation();
        displayLocation();

        binding.fbFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                }else if(mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
            }
        });

        binding.tvRute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRute = true;
                displayLocation();
            }
        });

    }


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        Utils.mLastLocation = location;
                        if (Utils.mLastLocation != null) {
                            final double latitudeUser = Utils.mLastLocation.getLatitude();
                            final double longitudeUser= Utils.mLastLocation.getLongitude();

                            if (mCurrentMarker != null){
                                mCurrentMarker.remove();
                            }

                            mMap.clear();


                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(new LatLng(latitudeUser, longitudeUser));

                            mCurrentMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(latitudeUser, longitudeUser))
                                    .title("Your location"));
                            Call<EventResponse> eventResponseCall = apiRequest.eventByIdRequest(idEvent);
                            eventResponseCall.enqueue(new Callback<EventResponse>() {
                                @Override
                                public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                                    if (response.body().getValue() == 1){
                                        Event event = response.body().getEventList().get(0);
//                    binding.setEvent(event);

                                        String tanggal = DateFormat.format("EEEE, dd MMM yyyy", event.getTglEvent()).toString();
                                        String jam = DateFormat.format("HH:mm", event.getTglEvent()).toString();
                                        Geocoder coder = new Geocoder(EventLocationActivity.this);
                                        List<Address> address;
                                        try {
                                            address = coder.getFromLocationName(event.getLokasi(),5);
                                            if (address==null) {

                                            }
                                            Address location=address.get(0);
                                            LatLng latLngEvent = new LatLng(location.getLatitude(), location.getLongitude());
                                            mMap.addMarker(new MarkerOptions().position(latLngEvent)
                                                    .title(""+event.getJudul())
                                                    .icon(BitmapDescriptorFactory
                                                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                            if (!displayRute) {
                                                getDirection(latLngEvent, false);
                                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngEvent, 17.0f));
                                            }else {

                                                int width = getResources().getDisplayMetrics().widthPixels;
                                                int height = getResources().getDisplayMetrics().heightPixels;
                                                int padding = (int) (Math.min(width, height)*0.2);
                                                builder.include(latLngEvent);
                                                LatLngBounds bounds = builder.build();
//                                                int padding = 500; // offset from edges of the map in pixels
                                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                                                mMap.moveCamera(cu);
                                                mMap.animateCamera(cu);

                                                if (directionTracking != null) {
                                                    directionTracking.remove();
                                                }
                                                getDirection(latLngEvent, true);
                                            }

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<EventResponse> call, Throwable t) {

                                }
                            });

                        } else {
                            Log.d("ERROR", "displayLocation: Cannot get your location");
                        }
                    }
                });
    }

    private void setUpLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            buildLocationCallback();
            buildLocationRequest();
        }
    }

    private void buildLocationRequest() {
        mLocationReqeust = new LocationRequest();
        mLocationReqeust.setInterval(UPDATE_INTERVAL);
        mLocationReqeust.setFastestInterval(FASTEST_INTERVAL);
        mLocationReqeust.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationReqeust.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    Utils.mLastLocation = location;
                }
            }
        };
    }


    private void loadDataEvent() {
        Call<EventResponse> eventResponseCall = apiRequest.eventByIdRequest(idEvent);
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body().getValue() == 1){
                    Event event = response.body().getEventList().get(0);
                    binding.setEvent(event);
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });
    }

    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(binding.mapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                70,
                100);
        mapAnimation.setDuration(800);


        ViewWeightAnimationWrapper lvInfoEventAnimationWrapper = new ViewWeightAnimationWrapper(binding.lvInfoEvent);
        ObjectAnimator infoAnimation = ObjectAnimator.ofFloat(lvInfoEventAnimationWrapper,
                "weight",
                30,
                0);
        infoAnimation.setDuration(800);

        mapAnimation.start();
        infoAnimation.start();
    }

    private void contractMapAnimation(){ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(binding.mapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                70);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper lvInfoEventAnimationWrapper = new ViewWeightAnimationWrapper(binding.lvInfoEvent);
        ObjectAnimator infoAnimation = ObjectAnimator.ofFloat(lvInfoEventAnimationWrapper,
                "weight",
                0,
                30);
        infoAnimation.setDuration(800);

        mapAnimation.start();
        infoAnimation.start();
    }

    private void getDirection(LatLng latLngEvent, boolean getRute){
        LatLng currentPosition = new LatLng(Utils.mLastLocation.getLatitude(), Utils.mLastLocation.getLongitude());
        String requestApi = null;
        try {
            requestApi ="https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+currentPosition.latitude+","+currentPosition.longitude+"&"+
                    "destination="+latLngEvent.latitude+","+latLngEvent.longitude+"&"+
                    "key="+getResources().getString(R.string.google_direction_key);
            Log.d("MANTAP DJIWA URL : ",requestApi);
            Log.e(TAG, "getDirection: "+requestApi);
            mapsApiRequest.getPath(requestApi).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        if(!getRute) {
                            JSONObject jsonObject = new JSONObject(response.body());
                            JSONArray routes = jsonObject.getJSONArray("routes");
                            JSONObject object = routes.getJSONObject(0);
                            JSONArray legs = object.getJSONArray("legs");
                            JSONObject objectLegs = legs.getJSONObject(0);

                            JSONObject distance = objectLegs.getJSONObject("distance");
                            String distanceText = distance.getString("text");


                            JSONObject time = objectLegs.getJSONObject("duration");
                            String timeText = time.getString("text");

                            binding.setJarak(distanceText);
                            binding.setWaktu(timeText);
                        }else {
                            new ParserTask().execute(response.body().toString());
                        }
                    } catch (Exception e) {
                        Toast.makeText(EventLocationActivity.this, "Terjadi error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);


        buildLocationRequest();
        buildLocationCallback();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationReqeust, locationCallback, Looper.myLooper());


        // Add a marker in Sydney and move the camera

    }



    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        ProgressDialog mDialog = new ProgressDialog(EventLocationActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Please waiting ...");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionJSONParser parser = new DirectionJSONParser();
                routes = parser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            mDialog.dismiss();
            ArrayList points = null;
            PolylineOptions polylineOptions = null;

            for (int i=0; i<lists.size(); i++){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);

                for (int k=0; k<path.size(); k++){
                    HashMap<String, String> point = path.get(k);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.RED);
                polylineOptions.geodesic(true);
            }

            if (polylineOptions != null){
                directionTracking = mMap.addPolyline(polylineOptions);
            }
        }
    }

}