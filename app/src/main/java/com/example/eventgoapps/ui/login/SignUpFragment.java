package com.example.eventgoapps.ui.login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.example.eventgoapps.databinding.FragmentSignUpBinding;
import com.example.eventgoapps.util.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.example.eventgoapps.util.Utils.ID_USER_KEY;
import static com.example.eventgoapps.util.Utils.LOGIN_STATUS;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    FrameLayout parentFrameLayout;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private boolean isAtLeast8 = false, hasUppercase = false, hasNumber = false, isRegistrationClickable = false;
    String idUser;
    private ProgressDialog progressDialog;
    public static final String TAG = SignUpFragment.class.getSimpleName();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ApiRequest apiRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);

        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Proses ...");

        binding.etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registrationDataCheck();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isConnectionInternet(getActivity())){
                    checkInput();
                }else {
                    Toast.makeText(getActivity(), "Tidak ada jaringan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FrameLayout parentLayout = ((LoginActivity)binding.getRoot().getContext()).binding.frameLogin;
        FragmentTransaction transaction =getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(parentLayout.getId(), fragment);
        transaction.commit();
    }

    @SuppressLint("ResourceType")
    private void registrationDataCheck() {
        String password = binding.etPassword.getText().toString(), email = binding.etEmail.getText().toString();

        if (password.length() >= 8) {
            isAtLeast8 = true;
            binding.pItem1IconParent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckOk)));
        } else {
            isAtLeast8 = false;
            binding.pItem1IconParent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckNo)));
        }
        if (password.matches("(.*[A-Z].*)")) {
            hasUppercase = true;
            binding.pItem2IconParent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckOk)));
        } else {
            hasUppercase = false;
            binding.pItem2IconParent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckNo)));
        }
        if (password.matches("(.*[0-9].*)")) {
            hasNumber = true;
            binding.pItem3IconParent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckOk)));
        } else {
            hasNumber = false;
            binding.pItem3IconParent.setCardBackgroundColor(Color.parseColor(getString(R.color.colorCheckNo)));
        }

    }


    private void checkInput() {
        if (!TextUtils.isEmpty(binding.etEmail.getText())) {
            if (binding.etEmail.getText().toString().matches(emailPattern)) {
                if (!TextUtils.isEmpty(binding.etNama.getText())) {
                    if (!TextUtils.isEmpty(binding.etPassword.getText())) {
                        if (!TextUtils.isEmpty(binding.etConfirmPassword.getText())) {
                            if (binding.etPassword.length() >= 8) {
                                if (isAtLeast8 && hasUppercase && hasNumber) {
                                    if (binding.etPassword.getText().toString().equals(binding.etConfirmPassword.getText().toString())){
                                        daftar();
                                    }else {
                                        binding.etConfirmPassword.setError("Password tidak cocok");
                                        Utils.scrollToView(binding.svSignUp, binding.etConfirmPassword);
                                    }
                                } else {
                                    binding.etPassword.setError("Password tidak sesuai");
                                    Utils.scrollToView(binding.svSignUp, binding.etPassword);
                                }
                            } else {
                                binding.etPassword.setError("Password terlalu sedikit");
                                Utils.scrollToView(binding.svSignUp, binding.etPassword);
                            }
                        }else {
                            binding.etConfirmPassword.setError("Konfirmasi password tidak boleh kosong");
                            Utils.scrollToView(binding.svSignUp, binding.etConfirmPassword);
                        }
                    }else {
                        binding.etPassword.setError("Password tidak boleh kosong");
                        Utils.scrollToView(binding.svSignUp, binding.etPassword);
                    }
                } else {
                    binding.etNama.setError("Nama tidak boleh kosong");
                    Utils.scrollToView(binding.svSignUp, binding.etNama);
                }
            }else {
                binding.etEmail.setError("Email tidak sesuai format");
                Utils.scrollToView(binding.svSignUp, binding.etEmail);
            }
        }else {
            binding.etEmail.setError("Email tidak boleh kosong");
            Utils.scrollToView(binding.svSignUp, binding.etEmail);
        }
    }

    private void daftar() {
        String nama = binding.etNama.getText().toString();
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        if (binding.etConfirmPassword.getText().toString().equals(password)) {
            progressDialog.show();
            Call<LoginResponse> loginResponseCall = apiRequest.registerUserRequest(nama, email, password);
            loginResponseCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    LoginResponse loginResponse = response.body();
                    Toast.makeText(getActivity(), ""+loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    if (loginResponse.getValue() == 0){
                        progressDialog.dismiss();
                        binding.btnSignUp.setEnabled(true);
                    }else {
                        progressDialog.dismiss();
                        idUser = loginResponse.getUser().getIdUser();
                        editor.putString(ID_USER_KEY, idUser);
                        editor.putBoolean(LOGIN_STATUS, true);
                        editor.commit();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                        getActivity().finish();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }else {
            binding.etConfirmPassword.setError("Password tidak cocok");
        }
    }
}