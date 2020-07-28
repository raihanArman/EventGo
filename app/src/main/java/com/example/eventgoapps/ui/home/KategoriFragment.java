package com.example.eventgoapps.ui.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.databinding.FragmentKategoriBinding;
import com.example.eventgoapps.ui.adapter.ExploreAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class KategoriFragment extends Fragment {

    FragmentKategoriBinding binding;
    ApiRequest apiRequest;
    String title, idKategori;
    ExploreAdapter exploreAdapter;

    public KategoriFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kategori, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        title = getArguments().getString("title");
        idKategori = getArguments().getString("id_kategori");

        binding.setTitle(title);

        exploreAdapter = new ExploreAdapter(getActivity());
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        binding.rvEvent.setLayoutManager(layoutManager);
        binding.rvEvent.setAdapter(exploreAdapter);

        if(!idKategori.equals("0")) {
            loadData();
        }else {
            loadAllDataEvent();
        }

        binding.btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPencarian();
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

    }

    private void loadAllDataEvent() {
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


    private void loadPencarian() {
        String cari = binding.etCari.getText().toString();
        if (!idKategori.equals("0")) {
            Call<EventResponse> eventResponseCall = apiRequest.eventKategoriCariRequest(idKategori, cari);
            eventResponseCall.enqueue(new Callback<EventResponse>() {
                @Override
                public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                    if (response.body().getValue() == 1) {
                        exploreAdapter.setEventList(response.body().getEventList());

                        binding.tvHasil.setVisibility(View.VISIBLE);
                        binding.tvHasil.setText("Hasil pencarian '" + cari + "' ...");

                        if (response.body().getEventList().size() <= 0) {
                            Toast.makeText(getActivity(), "Tidak ada data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<EventResponse> call, Throwable t) {

                }
            });
        }else {
            Call<EventResponse> eventResponseCall = apiRequest.eventSearchRequest(cari);
            eventResponseCall.enqueue(new Callback<EventResponse>() {
                @Override
                public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                    if (response.body().getValue() == 1){
                        exploreAdapter.setEventList(response.body().getEventList());
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

    }

    private void loadData(){
        Call<EventResponse> eventResponseCall = apiRequest.eventKategoriRequest(idKategori);
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