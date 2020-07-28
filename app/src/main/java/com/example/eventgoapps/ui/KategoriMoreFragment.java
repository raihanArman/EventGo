package com.example.eventgoapps.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.response.KategoriResponse;
import com.example.eventgoapps.databinding.FragmentKategoriMoreBinding;
import com.example.eventgoapps.ui.adapter.KategoriMoreAdapter;
import com.example.eventgoapps.ui.adapter.PesanAdapter;
import com.example.eventgoapps.util.TimeStampFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KategoriMoreFragment extends Fragment {

    ApiRequest apiRequest;
    FragmentKategoriMoreBinding binding;
    KategoriMoreAdapter kategoriMoreAdapter;

    public KategoriMoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kategori_more, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        kategoriMoreAdapter = new KategoriMoreAdapter(getActivity());


        kategoriMoreAdapter = new KategoriMoreAdapter(getActivity());
        binding.rvKategori.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvKategori.setAdapter(kategoriMoreAdapter);

        loadData();

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

    }

    private void loadData() {
        Call<KategoriResponse> kategoriResponseCall = apiRequest.kategoriAllRequest();
        kategoriResponseCall.enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                if (response.body().getValue() == 1) {
                    kategoriMoreAdapter.setKategoriList(response.body().getKategoriList());
                } else {
                    Toast.makeText(getActivity(), "Tidak ada data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {

            }
        });
    }
}