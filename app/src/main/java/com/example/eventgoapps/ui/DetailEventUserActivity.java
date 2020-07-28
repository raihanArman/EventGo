package com.example.eventgoapps.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.data.remote.model.Lampiran;
import com.example.eventgoapps.data.remote.model.Value;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.databinding.ActivityDetailEventBinding;
import com.example.eventgoapps.databinding.ActivityDetailEventUserBinding;
import com.example.eventgoapps.ui.adapter.LampiranAdapter;
import com.example.eventgoapps.ui.buat_event.BuatEventActivity;
import com.example.eventgoapps.util.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailEventUserActivity extends AppCompatActivity {

    ActivityDetailEventUserBinding binding;
    String idEvent, idUser, idRiwayat;
    int typeIntent;
    ApiRequest apiRequest;
    LampiranAdapter lampiranAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_event_user);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_event_user);
        idEvent = getIntent().getStringExtra("id_event");
        typeIntent = getIntent().getIntExtra("type_intent", 0);
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvLampiran.setLayoutManager(linearLayoutManager);
        lampiranAdapter = new LampiranAdapter(this);
        binding.rvLampiran.setAdapter(lampiranAdapter);

        loadDataEvent();
        loadLampiran();

        if (typeIntent == Utils.TYPE_INTENT_AKTIVITAS){
            binding.tvEdit.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.proses));
            binding.tvEdit.setText("Edit");
        }else if (typeIntent == Utils.TYPE_INTENT_RIWAYAT){
            idRiwayat = getIntent().getStringExtra("id_riwayat_user");
            Toast.makeText(this, ""+idRiwayat, Toast.LENGTH_SHORT).show();
            binding.tvEdit.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.red_main));
            binding.tvEdit.setText("Hapus");
        }

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (typeIntent == Utils.TYPE_INTENT_AKTIVITAS){
                    Intent intent = new Intent(DetailEventUserActivity.this, BuatEventActivity.class);
                    intent.putExtra("type_intent", Utils.TYPE_EDIT);
                    intent.putExtra("id_event", idEvent);
                    startActivity(intent);
                }else if (typeIntent == Utils.TYPE_INTENT_RIWAYAT){
                    deleteRiwayat();
                }
            }
        });

    }

    private void deleteRiwayat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pesan");
        builder.setMessage("Apakah anda yakin ingin menghapus riwayat event ini ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<Value> hapusCall = apiRequest.hapusRiwayatRequest(idRiwayat);
                hapusCall.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(DetailEventUserActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getValue() == 1) {
                                dialog.dismiss();
                                finish();
                            }else {
                                dialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(DetailEventUserActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void loadDataEvent() {
        Call<EventResponse> eventResponseCall = apiRequest.aktivitasByIdRequest(idEvent);
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

                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });
    }


    private void loadLampiran() {
        Call<List<Lampiran>> callLampiran = apiRequest.allLampiranRequest(idEvent);
        callLampiran.enqueue(new Callback<List<Lampiran>>() {
            @Override
            public void onResponse(Call<List<Lampiran>> call, Response<List<Lampiran>> response) {
                if (response.isSuccessful()){
                    List<Lampiran> lampiranList = response.body();
                    lampiranAdapter.setKajianList(lampiranList);
                }
            }

            @Override
            public void onFailure(Call<List<Lampiran>> call, Throwable t) {

            }
        });
    }

}