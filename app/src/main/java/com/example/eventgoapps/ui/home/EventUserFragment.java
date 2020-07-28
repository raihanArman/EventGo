package com.example.eventgoapps.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.eventgoapps.data.remote.model.Value;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.databinding.FragmentEventUserBinding;
import com.example.eventgoapps.ui.adapter.AktivitasAdapter;
import com.example.eventgoapps.ui.buat_event.BuatEventActivity;
import com.example.eventgoapps.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventUserFragment extends Fragment {

    ApiRequest apiRequest;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String idUser;
    FragmentEventUserBinding binding;
    AktivitasAdapter aktivitasAdapter;


    public EventUserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_user, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        idUser = sharedPreferences.getString(Utils.ID_USER_KEY, "");

        binding.lvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BuatEventActivity.class);
                intent.putExtra("type_intent", Utils.TYPE_ADD);
                getActivity().startActivity(intent);
            }
        });

        binding.lvAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BuatEventActivity.class);
                intent.putExtra("type_intent", Utils.TYPE_ADD);
                getActivity().startActivity(intent);
            }
        });

        aktivitasAdapter = new AktivitasAdapter(getActivity());
        binding.rvEvent.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvEvent.setAdapter(aktivitasAdapter);

    }

    private void cekEventUser() {
        Call<Value> valueCall = apiRequest.cekEventUserRequest(idUser);
        valueCall.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                if (response.body().getValue() == 1){
                    binding.lvEvent.setVisibility(View.VISIBLE);
                    binding.lvNoEvent.setVisibility(View.GONE);
                    loadDataAktivitas();
                }else {
                    binding.lvEvent.setVisibility(View.GONE);
                    binding.lvNoEvent.setVisibility(View.VISIBLE);
                    binding.setImage(R.drawable.no_event);
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {

            }
        });
    }
    private void loadDataAktivitas() {
        Call<EventResponse> eventResponseCall = apiRequest.aktivitasByUserRequest(idUser);
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                aktivitasAdapter.setEventResponseList(response.body().getEventList());
                if (response.body().getEventList().size() <= 0){
                    Toast.makeText(getActivity(), "Tidak ada data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        cekEventUser();
    }
}