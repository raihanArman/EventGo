package com.example.eventgoapps.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.User;
import com.example.eventgoapps.data.remote.model.Value;
import com.example.eventgoapps.data.remote.model.response.UserResponse;
import com.example.eventgoapps.databinding.ActivityEditProfilBinding;
import com.example.eventgoapps.util.ConvertBitmap;
import com.example.eventgoapps.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfilActivity extends AppCompatActivity implements ConvertBitmap {

    private static final String TAG = "EditProfilActivity";

    public static final int REQUEST_GALERY = 96;
    public static final int REQUEST_CAMERA = 98;
    public static final int CAMERA_PERMISSION = 90;

    ApiRequest apiRequest;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String idUser;
    ActivityEditProfilBinding binding;

    Bitmap bitmap = null;
    String photoUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profil);

        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        sharedPreferences = getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        idUser = sharedPreferences.getString(Utils.ID_USER_KEY, "");

        loadDataUser();

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera();
            }
        });

        binding.ivUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCamera();
            }
        });

        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProses();
            }
        });

    }

    private void updateProses() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Proses...");
        progressDialog.show();
        if (photoUser != null) {
            new LoadBitmapConverterCallback(EditProfilActivity.this, EditProfilActivity.this::bitmapToString).execute();
        }

        Calendar calendar = Calendar.getInstance();
        String tgl = DateFormat.format("yyyy-MM-dd HH:mm:ss", calendar.getTime()).toString();

        User user = new User();
        user.setIdUser(idUser);
        user.setNama(binding.etNama.getText().toString());
        user.setAlamat(binding.etAlamat.getText().toString());
        user.setEmail(binding.etEmail.getText().toString());
        user.setNoHp(binding.etNoTelp.getText().toString());
        user.setTglUpdate(tgl);
        user.setFoto(photoUser);
        Call<Value> valueCall = apiRequest.editUserRequest(user);
        valueCall.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                Value value = response.body();
                if (value.getValue() == 1){
                    Toast.makeText(EditProfilActivity.this, "Edit berhasil", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(EditProfilActivity.this, "Edit Data gagal", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditProfilActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfilActivity.this);
        builder.setTitle("Pilih");
        String[] pilihJenis = {"Camera", "Galery"};
        builder.setItems(pilihJenis, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                }else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_GALERY);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            }else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GALERY){
            if(resultCode == RESULT_OK && data != null){
                Uri imageUri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    binding.ivUser.setImageBitmap(bitmap);
                    new LoadBitmapConverterCallback(this, EditProfilActivity.this::bitmapToString).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(requestCode == REQUEST_CAMERA){
            if (resultCode == RESULT_OK && data != null){
                bitmap = (Bitmap) data.getExtras().get("data");
                binding.ivUser.setImageBitmap(bitmap);
                new LoadBitmapConverterCallback(this, EditProfilActivity.this::bitmapToString).execute();
            }
        }
    }

    private void editProfil(String gambar){
        photoUser = gambar;
    }
    @Override
    public void bitmapToString(String imgConvert) {
        editProfil(imgConvert);
    }

    private class LoadBitmapConverterCallback extends AsyncTask<Void, Void, String> {

        private WeakReference<Context> weakContext;
        private ConvertBitmap convertBitmap;

        public LoadBitmapConverterCallback(Context context, ConvertBitmap convertBitmap){
            this.weakContext = new WeakReference<>(context);
            this.convertBitmap = convertBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakContext.get();
        }

        @Override
        protected String doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] imgByte = byteArrayOutputStream.toByteArray();
            String imgBitmap = Base64.encodeToString(imgByte, Base64.DEFAULT);
            return imgBitmap;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            convertBitmap.bitmapToString(s);
        }
    }
}