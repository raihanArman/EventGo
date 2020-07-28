package com.example.eventgoapps.ui.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.databinding.FragmentExploreBinding;
import com.example.eventgoapps.ui.adapter.ExploreAdapter;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {

    FragmentExploreBinding binding;
    ApiRequest apiRequest;
    ExploreAdapter exploreAdapter;

    public ExploreFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);

        exploreAdapter = new ExploreAdapter(getActivity());
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        binding.rvExplore.setLayoutManager(layoutManager);
        binding.rvExplore.setAdapter(exploreAdapter);

        binding.tvMinggu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvMinggu.setBackgroundResource(R.drawable.bg_status);
                binding.tvMinggu.setTextColor(Color.parseColor("#ffffff"));
                binding.tvSemua.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvSemua.setTextColor(Color.parseColor("#000000"));
                binding.tvGratis.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvGratis.setTextColor(Color.parseColor("#000000"));
                binding.tvBerbayar.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvBerbayar.setTextColor(Color.parseColor("#000000"));
                binding.tvHasil.setVisibility(View.GONE);
                loadDataMingguIni();
            }
        });

        binding.tvGratis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvMinggu.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvMinggu.setTextColor(Color.parseColor("#000000"));
                binding.tvSemua.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvSemua.setTextColor(Color.parseColor("#000000"));
                binding.tvGratis.setBackgroundResource(R.drawable.bg_status);
                binding.tvGratis.setTextColor(Color.parseColor("#ffffff"));
                binding.tvBerbayar.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvBerbayar.setTextColor(Color.parseColor("#000000"));
                binding.tvHasil.setVisibility(View.GONE);
                loadEventByJenis("Gratis");
            }
        });
        binding.tvBerbayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvMinggu.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvMinggu.setTextColor(Color.parseColor("#000000"));
                binding.tvSemua.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvSemua.setTextColor(Color.parseColor("#000000"));
                binding.tvGratis.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvGratis.setTextColor(Color.parseColor("#000000"));
                binding.tvBerbayar.setBackgroundResource(R.drawable.bg_status);
                binding.tvBerbayar.setTextColor(Color.parseColor("#ffffff"));
                binding.tvHasil.setVisibility(View.GONE);
                loadEventByJenis("Berbayar");
            }
        });
        binding.tvSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvMinggu.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvMinggu.setTextColor(Color.parseColor("#000000"));
                binding.tvSemua.setBackgroundResource(R.drawable.bg_status);
                binding.tvSemua.setTextColor(Color.parseColor("#ffffff"));
                binding.tvGratis.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvGratis.setTextColor(Color.parseColor("#000000"));
                binding.tvBerbayar.setBackgroundResource(R.drawable.bg_status_2);
                binding.tvBerbayar.setTextColor(Color.parseColor("#000000"));
                binding.tvHasil.setVisibility(View.GONE);
                loadDataSemua();
            }
        });

        binding.btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPencarian();
            }
        });

        loadDataSemua();
    }

    private void loadEventByJenis(String jenis){
        Call<EventResponse> eventResponseCall = apiRequest.eventByJenisRequest(jenis);
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body().getValue() == 1){
                    exploreAdapter.setEventList(response.body().getEventList());
                    if (response.body().getEventList().size() <= 0){
                        Toast.makeText(getActivity(), "Tidak ada data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });
    }

    private void loadPencarian() {
        String cari = binding.etCari.getText().toString();
        Call<EventResponse> eventResponseCall = apiRequest.eventSearchRequest(cari);
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body().getValue() == 1){
                    exploreAdapter.setEventList(response.body().getEventList());
                    binding.tvMinggu.setBackgroundResource(R.drawable.bg_status_2);
                    binding.tvMinggu.setTextColor(Color.parseColor("#000000"));
                    binding.tvSemua.setBackgroundResource(R.drawable.bg_status);
                    binding.tvSemua.setTextColor(Color.parseColor("#ffffff"));
                    binding.tvGratis.setBackgroundResource(R.drawable.bg_status_2);
                    binding.tvGratis.setTextColor(Color.parseColor("#000000"));
                    binding.tvBerbayar.setBackgroundResource(R.drawable.bg_status_2);
                    binding.tvBerbayar.setTextColor(Color.parseColor("#000000"));

                    binding.tvHasil.setVisibility(View.VISIBLE);
                    binding.tvHasil.setText("Hasil pencarian '"+cari+"' ...");

                    if (response.body().getEventList().size() <= 0){
                        Toast.makeText(getActivity(), "Tidak ada data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });

    }

    private void loadDataSemua() {
        Call<EventResponse> eventResponseCall = apiRequest.eventAllRequest();
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body().getValue() == 1){
                    exploreAdapter.setEventList(response.body().getEventList());
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });
    }

    private void loadDataMingguIni(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String senin = DateFormat.format("yyy-MM-dd", c.getTime()).toString();

        Calendar cMinggu = Calendar.getInstance();
        cMinggu.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cMinggu.add(Calendar.DATE, 7);
        String minggu = DateFormat.format("yyy-MM-dd", cMinggu.getTime()).toString();

        Call<EventResponse> eventResponseCall = apiRequest.eventByWeekRequest(senin+" 00:00:00", minggu+" 23:59:00");
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body().getValue() == 1){
                    exploreAdapter.setEventList(response.body().getEventList());
                    if (response.body().getEventList().size() <= 0){
                        Toast.makeText(getActivity(), "Tidak ada data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });
    }
}