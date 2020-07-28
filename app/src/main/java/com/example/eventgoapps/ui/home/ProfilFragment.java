package com.example.eventgoapps.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.eventgoapps.MainActivity;
import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Value;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.data.remote.model.response.UserResponse;
import com.example.eventgoapps.databinding.FragmentProfilBinding;
import com.example.eventgoapps.ui.EditProfilActivity;
import com.example.eventgoapps.ui.PengaturanFragment;
import com.example.eventgoapps.ui.adapter.AktivitasAdapter;
import com.example.eventgoapps.ui.adapter.RiwayatAdapter;
import com.example.eventgoapps.ui.login.LoginActivity;
import com.example.eventgoapps.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilFragment extends Fragment {

    ApiRequest apiRequest;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String idUser;
    FragmentProfilBinding binding;
    RiwayatAdapter riwayatAdapter;

    public ProfilFragment() {
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profil, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        idUser = sharedPreferences.getString(Utils.ID_USER_KEY, "");

        cekEventUser();

        riwayatAdapter = new RiwayatAdapter(getActivity());
        binding.rvRiwayat.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvRiwayat.setAdapter(riwayatAdapter);


        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfilActivity.class);
                getActivity().startActivity(intent);
            }
        });
        
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutProses();
            }
        });

        binding.ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frameLayout = ((MainActivity)binding.getRoot().getContext()).binding.frameMain;
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(frameLayout.getId(), new PengaturanFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }



    private void loadRiwayat(){
        Call<EventResponse> eventResponseCall = apiRequest.riwayatUserRequest(idUser);
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                riwayatAdapter.setEventResponseList(response.body().getEventList());
                if (response.body().getEventList().size() <= 0){
                    Toast.makeText(getActivity(), "Tidak ada data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });

    }

    private void cekEventUser() {
        Call<Value> valueCall = apiRequest.cekEventUserRequest(idUser);
        valueCall.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                if (response.body().getValue() == 1){
                    binding.lvRiwayat.setVisibility(View.VISIBLE);
                    binding.lvNoEvent.setVisibility(View.GONE);
                }else {
                    binding.lvRiwayat.setVisibility(View.GONE);
                    binding.lvNoEvent.setVisibility(View.VISIBLE);
                    binding.setImage(R.drawable.no_event);
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {

            }
        });
    }

    private void logoutProses() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Proses ...");
        progressDialog.show();
        Call<Value> valueCall = apiRequest.logoutRequest(idUser);
        valueCall.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                if (response.body().getValue() == 1){
                    editor.putInt(Utils.ID_USER_KEY, 0);
                    editor.putBoolean(Utils.LOGIN_STATUS, false);
                    editor.commit();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Gagal login", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {

            }
        });
    }

    private void loadDataUser() {
        Call<UserResponse> userResponseCall = apiRequest.userRequest(idUser);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.body().getValue() == 1){
                    binding.setUser(response.body().getUser());
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataUser();
        loadRiwayat();
    }
}