package com.example.eventgoapps.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.data.remote.model.Value;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.data.remote.model.response.LikeEventResponse;
import com.example.eventgoapps.databinding.ActivityDetailEventBinding;
import com.example.eventgoapps.ui.adapter.LikeEventAdapter;
import com.example.eventgoapps.util.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailEventActivity extends AppCompatActivity implements OnMapReadyCallback {

    ActivityDetailEventBinding binding;
    String idEvent, idUser;
    ApiRequest apiRequest;
    GoogleMap map;
    LikeEventAdapter adapter;

    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_event);
        idEvent = getIntent().getStringExtra("id_event");
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        sharedPreferences = getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        idUser = sharedPreferences.getString(Utils.ID_USER_KEY, "");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Proses ...");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_event);
        mapFragment.getMapAsync(this);

        loadDataEvent();

        adapter = new LikeEventAdapter(this);
        LinearLayoutManager layoutManagerHorizontal = new LinearLayoutManager(this);
        layoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.rvEventLike.setLayoutManager(layoutManagerHorizontal);
        binding.rvEventLike.addItemDecoration(new OverlapDecoration());
        binding.rvEventLike.setHasFixedSize(true);
        binding.rvEventLike.setAdapter(adapter);

        loadDataEventLike();
        checkLike();

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnFav.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(DetailEventActivity.this, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    addLike();
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return true;
            }
        });

        binding.rvLihatLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailEventActivity.this, EventLocationActivity.class);
                intent.putExtra("id_event", idEvent);
                startActivity(intent);
            }
        });

    }

    private void checkLike() {
        Call<Value> valueCall = apiRequest.cekLike(idEvent, idUser);
        valueCall.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                Value value = response.body();
                if (value.getValue() == 1){
                    binding.btnFav.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24));
                }else {
                    binding.btnFav.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_border_24));
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                Toast.makeText(DetailEventActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addLike() {
        progressDialog.show();
        Call<Value> valueCall = apiRequest.addLike(idEvent, idUser);
        valueCall.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                Value value = response.body();
                if (value.getValue() == 1){
                    binding.btnFav.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_border_24));
                }else if(value.getValue() == 2){
                    binding.btnFav.setImageDrawable(getDrawable(R.drawable.ic_baseline_favorite_24));
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {

            }
        });
    }

    private void loadDataEventLike() {
        Call<LikeEventResponse> userResponseCall = apiRequest.eventLikeRequest(idEvent);
        userResponseCall.enqueue(new Callback<LikeEventResponse>() {
            @Override
            public void onResponse(Call<LikeEventResponse> call, Response<LikeEventResponse> response) {
                if (response.body().getValue() == 1){
                    if (response.body().getUserList().size() > 5) {
                        adapter.setUserList(response.body().getUserList());
                    }
                    binding.setLike(response.body().getUserList().size()+" like");
                }
            }

            @Override
            public void onFailure(Call<LikeEventResponse> call, Throwable t) {

            }
        });

    }

    private void loadDataEvent() {
        Call<EventResponse> eventResponseCall = apiRequest.eventByIdRequest(idEvent);
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body().getValue() == 1){
                    Event event = response.body().getEventList().get(0);
                    binding.setEvent(event);

                    String tanggal = DateFormat.format("EEEE, dd MMM yyyy", event.getTglEvent()).toString();
                    String jam = DateFormat.format("HH:mm", event.getTglEvent()).toString();

                    binding.setJam(jam);
                    binding.setTanggal(tanggal);

                    if (map != null){
                        map.clear();
                    }

                    Geocoder coder = new Geocoder(DetailEventActivity.this);
                    List<Address> address;
                    try {
                        address = coder.getFromLocationName(event.getLokasi(),5);
                        if (address==null) {

                        }
                        Address location=address.get(0);
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng)
                                .title(""+event.getJudul()));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0f));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney and move the camera
        map = googleMap;
    }
    public class OverlapDecoration extends RecyclerView.ItemDecoration {

        private final static int vertOverlap = -20;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            final int itemPosition = parent.getChildAdapterPosition(view);

            outRect.set(0, 0, vertOverlap, 0);

        }
    }
}