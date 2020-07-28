package com.example.eventgoapps.ui.pesan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.response.PesanResponse;
import com.example.eventgoapps.databinding.ActivityPesanBinding;
import com.example.eventgoapps.ui.adapter.PesanAdapter;
import com.example.eventgoapps.util.TimeStampFormatter;
import com.example.eventgoapps.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesanActivity extends AppCompatActivity {

    ApiRequest apiRequest;
    SharedPreferences sharedPreferences;
    ActivityPesanBinding binding;
    String iduser;
    PesanAdapter pesanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesan);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pesan);

        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        sharedPreferences = getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        iduser = sharedPreferences.getString(Utils.ID_USER_KEY, "");

        pesanAdapter = new PesanAdapter(this, new TimeStampFormatter());
        binding.rvPesan.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPesan.setAdapter(pesanAdapter);

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void loadDataPesan() {
        Call<PesanResponse> pesanResponseCall = apiRequest.pesanAllRequest(iduser);
        pesanResponseCall.enqueue(new Callback<PesanResponse>() {
            @Override
            public void onResponse(Call<PesanResponse> call, Response<PesanResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getPesanList().size() > 0){
                        pesanAdapter.setPesanList(response.body().getPesanList());
                    }
                }
            }

            @Override
            public void onFailure(Call<PesanResponse> call, Throwable t) {
                Toast.makeText(PesanActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataPesan();
    }
}