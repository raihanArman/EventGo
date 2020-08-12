package com.example.eventgoapps.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.eventgoapps.MainActivity;
import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.data.remote.model.Token;
import com.example.eventgoapps.data.remote.model.User;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.data.remote.model.response.EventTerdekat;
import com.example.eventgoapps.data.remote.model.response.KategoriResponse;
import com.example.eventgoapps.data.remote.model.response.UserResponse;
import com.example.eventgoapps.databinding.FragmentHomeBinding;
import com.example.eventgoapps.ui.KategoriMoreFragment;
import com.example.eventgoapps.ui.pesan.PesanActivity;
import com.example.eventgoapps.ui.adapter.EventPopulerAdapter;
import com.example.eventgoapps.ui.adapter.EventTerbaruAdapter;
import com.example.eventgoapps.ui.adapter.EventTerdekatAdapter;
import com.example.eventgoapps.ui.adapter.KategoriAdapter;
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
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    FragmentHomeBinding binding;
    ApiRequest apiRequest;
    SharedPreferences sharedPreferences;
    String idUser, lokasi;

    KategoriAdapter kategoriAdapter;
    EventTerbaruAdapter eventTerbaruAdapter;
    EventPopulerAdapter eventPopulerAdapter;
    EventTerdekatAdapter eventTerdekatAdapter;

    List<String> listIdEvent;

    DatabaseReference db;
    GeoFire geoFire;
    int distance = 1;
    public static final int LIMIT = 10;

    private static final int MY_PERMISSION_REQUEST_CODE = 732;
    public static final int UPDATE_INTERVAL = 1000;
    public static final int FASTEST_INTERVAL = 5000;
    public static final int DISPLACEMENT = 10;

    private LocationRequest mLocationReqeust;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;


    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString(Utils.ID_USER_KEY, "");
        listIdEvent = new ArrayList<>();

        db = FirebaseDatabase.getInstance().getReference(Utils.EVENT_LOCATION);
        geoFire = new GeoFire(db);

        binding.setLokasi(lokasi);
        kategoriAdapter = new KategoriAdapter(getActivity());
        eventTerbaruAdapter = new EventTerbaruAdapter(getActivity());
        eventPopulerAdapter = new EventPopulerAdapter(getActivity());
        eventTerdekatAdapter = new EventTerdekatAdapter(getActivity());

        LinearLayoutManager layoutManagerHorizontal = new LinearLayoutManager(getActivity());
        layoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvKategori.setLayoutManager(layoutManagerHorizontal);
        binding.rvKategori.setAdapter(kategoriAdapter);
        LinearLayoutManager layoutManagerHorizontal1 = new LinearLayoutManager(getActivity());
        layoutManagerHorizontal1.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvLatest.setLayoutManager(layoutManagerHorizontal1);
        binding.rvLatest.setAdapter(eventTerbaruAdapter);
        LinearLayoutManager layoutManagerHorizontal2 = new LinearLayoutManager(getActivity());
        layoutManagerHorizontal2.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvFavorite.setLayoutManager(layoutManagerHorizontal2);
        binding.rvFavorite.setAdapter(eventPopulerAdapter);


        LinearLayoutManager layoutManagerHorizontal3 = new LinearLayoutManager(getActivity());
        layoutManagerHorizontal3.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvTerdekat.setLayoutManager(layoutManagerHorizontal3);
        binding.rvTerdekat.setAdapter(eventTerdekatAdapter);



        loadDataKategori();
        loadEventTerbaru();
        loadEventPopuler();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        setUpLocation();
        displayLocation();


        binding.tvMoreTerdekat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsEventTerdekatActivity.class);
                startActivity(intent);
            }
        });

        binding.ivNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PesanActivity.class);
                startActivity(intent);
            }
        });

        binding.tvEventMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frameLayout = ((MainActivity)binding.getRoot().getContext()).binding.frameMain;
                FragmentManager fm = ((FragmentActivity)binding.getRoot().getContext()).getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                KategoriFragment kategoriFragment = new KategoriFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id_kategori", "0");
                bundle.putString("title", "Semua Event");
                kategoriFragment.setArguments(bundle);
                transaction.replace(frameLayout.getId(), kategoriFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }



    private void loadEventPopuler() {
        Call<EventResponse> eventResponseCall = apiRequest.eventFavoriteRequest();
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body().getValue() == 1) {
                    eventPopulerAdapter.setEventList(response.body().getEventList());;
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });
    }

    private void loadEventTerbaru() {
        Call<EventResponse> eventResponseCall = apiRequest.eventAllRequest();
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body().getValue() == 1) {
                    eventTerbaruAdapter.setEventList(response.body().getEventList());
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });
    }

    private void loadDataUser() {
        Call<UserResponse> userResponseCall = apiRequest.userRequest(idUser);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.body().getValue() == 1) {
                    User user = response.body().getUser();
                    binding.setUser(user);
                    if (user.getJumNotif().equals("0") || user.getJumNotif().equals("") || user.getJumNotif() == null){
                        binding.rvNotif.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }



    private void loadDataKategori() {
        Call<KategoriResponse> kategoriResponseCall = apiRequest.kategoriAllRequest();
        kategoriResponseCall.enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                if (response.body().getValue() == 1) {
                    kategoriAdapter.setKategoriList(response.body().getKategoriList());
                } else {
                    Toast.makeText(getActivity(), "Tidak ada data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {

            }
        });
    }


    private void loadDataTerdekat(){
        listIdEvent.clear();
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(Utils.mLastLocation.getLatitude(), Utils.mLastLocation.getLongitude()), 4);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (listIdEvent != null){
                    if (!listIdEvent.contains(key)){
                        listIdEvent.add(key);
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
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, MY_PERMISSION_REQUEST_CODE);
        } else {
            buildLocationCallback();
            buildLocationRequest();
        }
    }


    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission((Activity) getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission((Activity) getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        Utils.mLastLocation = location;
                        if (Utils.mLastLocation != null) {
                            final double latitude = Utils.mLastLocation.getLatitude();
                            final double longitude = Utils.mLastLocation.getLongitude();

                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder((Activity) getContext(), Locale.getDefault());
                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                String kota = addresses.get(0).getSubAdminArea();
                                String[] splitKota = kota.split(" ");
                                String kecamatan = addresses.get(0).getLocality();
                                String[] splitKecamatan = kecamatan.split(" ");
                                binding.setLokasi(splitKota[1] + ", " + splitKecamatan[1]);

                                loadDataTerdekat();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Log.d("ERROR", "displayLocation: Cannot get your location");
                        }
                    }
                });
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
        if (ActivityCompat.checkSelfPermission((Activity) getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission((Activity) getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onPause() {
        super.onPause();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataUser();
        if (fusedLocationProviderClient != null) {
            buildLocationCallback();
            buildLocationRequest();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateFirebaseToken();
    }

    private void updateFirebaseToken(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference(Utils.TOKEN_TBL);
        Token token = new Token(FirebaseInstanceId.getInstance().getToken());
        tokens.child(idUser)
                .setValue(token);
    }

}