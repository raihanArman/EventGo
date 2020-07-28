package com.example.eventgoapps.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.eventgoapps.MainActivity;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.receiver.NearbyReceiver;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoadEventTerdekat extends View {
    Context context;
    DatabaseReference db, dbEvent;
    GeoFire geoFire;
    SharedPreferences sharedPreferences;

    private static final String TAG = "LoadEventTerdekat";

    private static final int MY_PERMISSION_REQUEST_CODE = 732;
    public static final int UPDATE_INTERVAL = 5000;
    public static final int FASTEST_INTERVAL = 5000;
    public static final int DISPLACEMENT = 10;

    private LocationRequest mLocationReqeust;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    List<String> idEventList = new ArrayList<>();

    boolean eventFound = false;

    public LoadEventTerdekat(Context context) {
        super(context);
        this.context = context;
        dbEvent = FirebaseDatabase.getInstance().getReference(Utils.DATA_EVENT);
        db = FirebaseDatabase.getInstance().getReference(Utils.EVENT_LOCATION);
        geoFire = new GeoFire(db);
        sharedPreferences = context.getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        setUpLocation();
        displayLocation();


    }

    public Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            displayLocation();
            mHandler.sendEmptyMessageDelayed(0, 60000);
            return false;
        }
    });

    private void setUpLocation() {
        buildLocationCallback();
        buildLocationRequest();
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
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Utils.mLastLocation = location;
                        if (Utils.mLastLocation != null) {
                            loadData();
                        } else {
                            Log.d("ERROR", "displayLocation: Cannot get your location");
                        }
                    }
                });
    }

    private void loadData(){
//        idEventList.clear();
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(Utils.mLastLocation.getLatitude(), Utils.mLastLocation.getLongitude()), 1);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                dbEvent.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Event event = dataSnapshot.getValue(Event.class);
//                        if (!idEventList.contains(event.getIdEvent())){
//                            idEventList.add(event.getIdEvent());
//                        }
//                        Todo:Notif
                        boolean statusNotif = sharedPreferences.getBoolean(Utils.NOTIF_TERDEKAT_STATUS, false);
                        if (statusNotif) {
                            Intent intent = new Intent(context, NearbyReceiver.class);
                            intent.putExtra("data_event", event);
                            intent.setAction(Utils.NOTIF_EVENT_NEARBY);
                            context.sendBroadcast(intent);
                            Log.d(TAG, "onDataChange: Event terdekat : " + event.getJudul());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

}
