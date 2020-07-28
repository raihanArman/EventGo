package com.example.eventgoapps.ui.home;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.MapsApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.data.remote.model.response.EventTerdekat;
import com.example.eventgoapps.databinding.ActivityMapsEventTerdekatBinding;
import com.example.eventgoapps.ui.EventLocationActivity;
import com.example.eventgoapps.ui.adapter.EventTerdekatAdapter;
import com.example.eventgoapps.util.Utils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsEventTerdekatActivity extends FragmentActivity implements OnMapReadyCallback {

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


    EventTerdekatAdapter eventTerdekatAdapter;

    private LocationRequest mLocationReqeust;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;

    Marker mCurrentMarker;
    DatabaseReference db;
    GeoFire geoFire;
    int distance = 1;
    public static final int LIMIT = 10;
    List<String> listIdEvent;


    ActivityMapsEventTerdekatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_maps_event_terdekat);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listIdEvent = new ArrayList<>();
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        mapsApiRequest = MapsApiRequest.getClient(Utils.mapsUrl).create(ApiRequest.class);

        db = FirebaseDatabase.getInstance().getReference(Utils.EVENT_LOCATION);
        geoFire = new GeoFire(db);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        setUpLocation();
        displayLocation();


        eventTerdekatAdapter = new EventTerdekatAdapter(this);
        LinearLayoutManager layoutManagerHorizontal3 = new LinearLayoutManager(this);
        layoutManagerHorizontal3.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvEvent.setLayoutManager(layoutManagerHorizontal3);
        binding.rvEvent.setAdapter(eventTerdekatAdapter);


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

                            if (mCurrentMarker != null){
                                mCurrentMarker.remove();
                            }

                            loadDataTerdekat(new LatLng(latitudeUser, longitudeUser));
                        } else {
                            Log.d("ERROR", "displayLocation: Cannot get your location");
                        }
                    }
                });
    }

    private void loadDataTerdekat(LatLng latLng){
        listIdEvent.clear();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng);

        mCurrentMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("You"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(Utils.mLastLocation.getLatitude(), Utils.mLastLocation.getLongitude()), 3);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (listIdEvent != null){
                    if (!listIdEvent.contains(key)){
                        listIdEvent.add(key);
                        builder.include(new LatLng(location.latitude, location.longitude));
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(location.latitude, location.longitude))
                                .flat(true)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        );
                    }
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (Math.min(width, height)*0.2);
                LatLngBounds bounds = builder.build();
//                                                int padding = 500; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

                mMap.moveCamera(cu);
                mMap.animateCamera(cu);
                List<Event> eventList = new ArrayList<>();
                DatabaseReference db = FirebaseDatabase.getInstance().getReference(Utils.DATA_EVENT);
                db.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            EventTerdekat eventTerdekat = snapshot.getValue(EventTerdekat.class);
                            Event event = new Event();
                            event.setPamfletUrl(eventTerdekat.getPamflet());
                            event.setTglEvent(eventTerdekat.getTglEvent());
                            event.setIdEvent(eventTerdekat.getIdEvent());
                            event.setJudul(eventTerdekat.getJudul());
                            event.setLokasi(eventTerdekat.getLokasi());

                            if (listIdEvent.contains(event.getIdEvent())) {
                                eventList.add(event);
                            }
                        }
                        eventTerdekatAdapter.setEventList(eventList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

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
    public void onMapReady(GoogleMap googleMap) {mMap = googleMap;
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
    }
}