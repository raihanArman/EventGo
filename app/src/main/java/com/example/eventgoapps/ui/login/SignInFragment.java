package com.example.eventgoapps.ui.login;

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

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.eventgoapps.MainActivity;
import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.response.LoginResponse;
import com.example.eventgoapps.databinding.FragmentSignInBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.eventgoapps.util.Utils.ID_USER_KEY;
import static com.example.eventgoapps.util.Utils.LOGIN_KEY;
import static com.example.eventgoapps.util.Utils.LOGIN_STATUS;

public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";

    private FragmentSignInBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    ProgressDialog progressDialog;

    ApiRequest apiRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Memproses ...");
        sharedPreferences = getActivity().getSharedPreferences(LOGIN_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekForm();
            }
        });
        binding.tvRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
            }
        });

    }

    private void cekForm(){
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        if (!TextUtils.isEmpty(binding.etEmail.getText())){
            if (binding.etEmail.getText().toString().matches(emailPattern)) {
                if (!TextUtils.isEmpty(binding.etPassword.getText())){
                    progressDialog.show();
                    prosesLogin(email, password);
                }else {
                    binding.etPassword.setError("Password tidak boleh kosong");
                }
            } else {
                binding.etEmail.setError("Email tidak sesuai format");
            }
        }else {
            binding.etEmail.setError("Email tidak boleh kosong");
        }

    }

    private void prosesLogin(String email, String password) {
        Call<LoginResponse> loginResponseCall = apiRequest.loginUserRequest(email, password);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if (loginResponse.getValue() == 1){
                    String idUser = loginResponse.getUser().getIdUser();
                    editor.putString(ID_USER_KEY, idUser);
                    editor.putBoolean(LOGIN_STATUS, true);
                    editor.commit();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }else {
                    progressDialog.dismiss();
                    binding.btnSignIn.setEnabled(true);
                    Toast.makeText(getActivity(), ""+loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFragment(Fragment fragment){
        FrameLayout parentLayout =((LoginActivity)binding.getRoot().getContext()).binding.frameLogin;
        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(parentLayout.getId(), fragment);
        transaction.commit();
    }
}