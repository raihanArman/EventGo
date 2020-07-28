package com.example.eventgoapps.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.eventgoapps.MainActivity;
import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.databinding.ActivityLoginBinding;
import com.example.eventgoapps.util.Utils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    ActivityLoginBinding binding;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    ApiRequest apiRequest;

    DatabaseReference db;
    GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        sharedPreferences = getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon tunggu");
        progressDialog.setMessage("Proses ...");
        progressDialog.setCancelable(false);

        db = FirebaseDatabase.getInstance().getReference(Utils.EVENT_LOCATION);
        geoFire = new GeoFire(db);

        cekLogin();

    }

    private void cekLogin() {
        progressDialog.show();
        boolean statusLogin = sharedPreferences.getBoolean(Utils.LOGIN_STATUS, false);
        if (statusLogin){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            progressDialog.dismiss();
            setFragment(new SignInFragment());
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.frameLogin.getId(), fragment);
        transaction.commit();
    }
}