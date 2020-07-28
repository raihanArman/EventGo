package com.example.eventgoapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.eventgoapps.data.local.entity.EventLocation;
import com.example.eventgoapps.data.local.repository.EventLokasiRepository;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.data.remote.model.response.EventTerdekat;
import com.example.eventgoapps.databinding.ActivityMainBinding;
import com.example.eventgoapps.service.NearbyEventService;
import com.example.eventgoapps.ui.home.EventUserFragment;
import com.example.eventgoapps.ui.home.ExploreFragment;
import com.example.eventgoapps.ui.home.FavoriteFragment;
import com.example.eventgoapps.ui.home.HomeFragment;
import com.example.eventgoapps.ui.home.ProfilFragment;
import com.example.eventgoapps.util.ConvertBitmap;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public ActivityMainBinding binding;
    ApiRequest apiRequest;
    //    Simpan Lokasi
    EventLokasiRepository eventLokasiRepository;
    DatabaseReference db;
    GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        eventLokasiRepository = new EventLokasiRepository(getApplication());

        db = FirebaseDatabase.getInstance().getReference(Utils.EVENT_LOCATION);
        geoFire = new GeoFire(db);

        new SaveEventAsyntask(MainActivity.this).execute();

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home){
                    setFragment(new HomeFragment());
                }else if (item.getItemId() == R.id.nav_explore){
                    setFragment(new ExploreFragment());
                }else if (item.getItemId() == R.id.nav_profil){
                    setFragment(new ProfilFragment());
                }else if (item.getItemId() == R.id.nav_fav){
                    setFragment(new FavoriteFragment());
                }else if (item.getItemId() == R.id.nav_add){
                    setFragment(new EventUserFragment());
                }

                return true;
            }
        });

        setFragment(new HomeFragment());

    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, NearbyEventService.class));
    }

    public void setFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.frameMain.getId(), fragment);
        transaction.commit();
    }




    private class SaveEventAsyntask extends AsyncTask<Void, Void, Void> {

        private WeakReference<Context> weakContext;

        public SaveEventAsyntask(Context context){
            this.weakContext = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakContext.get();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference(Utils.DATA_EVENT);
            db.removeValue();
            Call<EventResponse> eventResponseCall = apiRequest.eventAllRequest();
            eventResponseCall.enqueue(new Callback<EventResponse>() {
                @Override
                public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                    EventLocation eventLocation;
                    List<EventLocation> eventLocations = eventLokasiRepository.getAllLokasi();
                    if (eventLocations.size() > 0){
                        eventLokasiRepository.cleanLokasi();
                    }
                    for (Event event : response.body().getEventList()){
                        eventLocation = new EventLocation();
                        eventLocation.setLokasi(event.getLokasi());
                        eventLocation.setIdEvent(event.getIdEvent());
                        eventLokasiRepository.insertLokasi(eventLocation);
                    }

                    for (Event event : response.body().getEventList()){
                        db.child(event.getIdEvent()).setValue(event);
                    }

                    List<EventLocation> eventLocationList = eventLokasiRepository.getAllLokasi();
                    Geocoder geocoder = new Geocoder(weakContext.get());
                    List<Address> addresses;
                    for (EventLocation event : eventLocationList){
                        try {
                            addresses = geocoder.getFromLocationName(event.getLokasi(),5);
                            Address location=addresses.get(0);
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            geoFire.setLocation(event.getIdEvent(), new GeoLocation(latLng.latitude, latLng.longitude), new GeoFire.CompletionListener() {
                                @Override
                                public void onComplete(String key, DatabaseError error) {

                                }
                            });
                        } catch (IOException e) {
                            Log.d(TAG, "onResponse: "+e.getMessage());
                        }
                    }

                }

                @Override
                public void onFailure(Call<EventResponse> call, Throwable t) {

                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: berhasil input");
        }
    }
}