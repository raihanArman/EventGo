package com.example.eventgoapps.ui.buat_event;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.local.repository.LampiranRepository;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.data.remote.model.Lampiran;
import com.example.eventgoapps.data.remote.model.Value;
import com.example.eventgoapps.databinding.FragmentFormLampiranBinding;
import com.example.eventgoapps.databinding.FragmentFormLampiranEditBinding;
import com.example.eventgoapps.util.ConvertBitmap;
import com.example.eventgoapps.util.Utils;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class FormLampiranEditFragment extends Fragment implements ConvertBitmap{

    private static final String TAG = "LampiranActivity";

    public static final int LAMPIRAN_1_CODE = 98;
    public static final int LAMPIRAN_2_CODE = 97;
    public static final int LAMPIRAN_3_CODE = 96;
    public static final int LAMPIRAN_4_CODE = 95;

    Bitmap bitmap;
    Map<String, String> lampiranMap = new HashMap<>();
    ApiRequest apiRequest;

    ProgressDialog progressDialog;
    boolean hasSuratLokasi = false, hasSuratKeramaian = false;

    Event event;
    int lampiranKey;
    String idUser;

    FragmentFormLampiranEditBinding binding;
    LampiranRepository lampiranRepository;

    DatabaseReference db;
    GeoFire geoFire;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form_lampiran_edit, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lampiranRepository = new LampiranRepository(getActivity().getApplication());
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);

        ((BuatEventActivity)binding.getRoot().getContext()).binding.nestedScrollView.scrollTo(0,0);

        BuatEventActivity.prosesInputEventDeskripsi = true;
        BuatEventActivity.statusLampiran = true;

        db = FirebaseDatabase.getInstance().getReference(Utils.EVENT_LOCATION);
        geoFire = new GeoFire(db);

        event = getArguments().getParcelable("event");
        idUser = getArguments().getString("id_user", "");
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Proses ...");

        Toast.makeText(getActivity(), "Tanggal Update : "+event.getTglUpdate(), Toast.LENGTH_SHORT).show();

        loadLampiran();

        binding.layoutBtnAddImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 2;
                startActivityForResult(intent, LAMPIRAN_2_CODE);
            }
        });

        binding.tvGantiGambar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 2;
                startActivityForResult(intent, LAMPIRAN_2_CODE);
            }
        });

        binding.layoutBtnAddImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 3;
                startActivityForResult(intent, LAMPIRAN_3_CODE);
            }
        });

        binding.tvGantiGambar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                lampiranKey = 3;
                startActivityForResult(intent, LAMPIRAN_3_CODE);
            }
        });


        binding.btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnectionInternet(getActivity())) {
                    if (hasSuratKeramaian){
                        if (hasSuratLokasi){
                            updateEvent();
                        }else {
                            Toast.makeText(getActivity(), "Anda belum memasukkan surat lokasi", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(getActivity(), "Anda belum memasukkan surat keramaian", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "Tidak ada jaringan", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadLampiran() {
        Call<List<Lampiran>> lampiranCall = apiRequest.allLampiranRequest(event.getIdEvent());
        lampiranCall.enqueue(new Callback<List<Lampiran>>() {
            @Override
            public void onResponse(Call<List<Lampiran>> call, Response<List<Lampiran>> response) {
                if (response.isSuccessful()){
                    for (Lampiran lampiran : response.body()){
                        if (lampiran.getKeterangan().equals("surat izin keramaian")){
                            binding.setKeramaian(lampiran.getGambar());
                            hasSuratKeramaian = true;
                        }

                        if (lampiran.getKeterangan().equals("surat peminjaman tempat")){
                            binding.setPeminjamanLokasi(lampiran.getGambar());
                            hasSuratLokasi = true;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Lampiran>> call, Throwable t) {
                Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Peringatan");
        builder.setMessage("Apakah anda yakin usulan event anda sudah tidak mau diubah ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Mengedit...");
                progressDialog.show();
                Call<Value> valueCall = apiRequest.editEventRequest(
                        event
                );
                valueCall.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getValue() == 1){
                                Geocoder geocoder = new Geocoder(getActivity());
                                List<Address> addresses;
                                try {
                                    addresses = geocoder.getFromLocationName(event.getLokasi(),5);
                                    Address location=addresses.get(0);
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    geoFire.setLocation(event.getIdEvent(), new GeoLocation(latLng.latitude, latLng.longitude), new GeoFire.CompletionListener() {
                                        @Override
                                        public void onComplete(String key, DatabaseError error) {
                                            if (lampiranMap.size() > 0) {
                                                for (Map.Entry<String, String> entry : lampiranMap.entrySet()) {
                                                    Log.d(TAG, "onResponse: Key : " + entry.getKey() + ", Value : " + entry.getValue());
                                                    Call<Value> callInputLampiran = apiRequest.inputLampiranRequest(
                                                            event.getIdEvent(),
                                                            entry.getKey(),
                                                            entry.getValue()
                                                    );
                                                    callInputLampiran.enqueue(new Callback<Value>() {
                                                        @Override
                                                        public void onResponse(Call<Value> call, Response<Value> response) {
                                                            Log.d(TAG, "onResponse: " + response.body().getMessage());
                                                        }

                                                        @Override
                                                        public void onFailure(Call<Value> call, Throwable t) {
                                                            Log.d(TAG, "onFailure: " + t.getMessage());
                                                        }
                                                    });
                                                }
                                            }
                                            progressDialog.dismiss();
                                            Toast.makeText(getActivity(), "Berhasil edit event", Toast.LENGTH_SHORT).show();
                                            getActivity().finish();
                                        }
                                    });
                                } catch (IOException e) {
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.show();
        if (requestCode == LAMPIRAN_2_CODE){
            if (resultCode == RESULT_OK && data != null){
                Uri imageUri = data.getData();
                binding.ivLampiran2.setVisibility(View.VISIBLE);
                binding.layoutBtnAddImage2.setVisibility(View.INVISIBLE);
                binding.tvGantiGambar2.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.ivLampiran2.setImageBitmap(bitmap);
                new LoadBitmapConvertAsync(getActivity(), FormLampiranEditFragment.this::bitmapToString).execute();
                hasSuratLokasi = true;
            }
            progressDialog.dismiss();
        }else if (requestCode == LAMPIRAN_3_CODE){
            if (resultCode == RESULT_OK && data != null){
                Uri imageUri = data.getData();
                binding.ivLampiran3.setVisibility(View.VISIBLE);
                binding.layoutBtnAddImage3.setVisibility(View.INVISIBLE);
                binding.tvGantiGambar3.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                binding.ivLampiran3.setImageBitmap(bitmap);
                new LoadBitmapConvertAsync(getActivity(), FormLampiranEditFragment.this::bitmapToString).execute();
                hasSuratKeramaian = true;
            }
            progressDialog.dismiss();
        }
    }


    @Override
    public void bitmapToString(String imgConvert) {
        progressDialog.dismiss();
        if (lampiranMap.containsKey(getKeteranganLampiran(lampiranKey))){
            lampiranMap.remove(getKeteranganLampiran(lampiranKey));
        }
        lampiranMap.put(getKeteranganLampiran(lampiranKey), imgConvert);
    }


    private String getKeteranganLampiran(int key){
        String keterangan = "";
        switch (key){
            case 1 :
                keterangan = "surat kesediaan pemateri";
                break;
            case 2 :
                keterangan = "surat peminjaman tempat";
                break;
            case 3 :
                keterangan = "surat izin keramaian";
                break;
            case 4 :
                keterangan = "surat kerjasama";
                break;
            default:
                return "salah keterangan";
        }

        return keterangan;
    }


    private class LoadBitmapConvertAsync extends AsyncTask<Void, Void, String> {

        private WeakReference<Context> weakContext;
        ConvertBitmap convertBitmap;

        public LoadBitmapConvertAsync(Context context, ConvertBitmap convertBitmap) {
            this.weakContext = new WeakReference<>(context);
            this.convertBitmap = convertBitmap;
        }

        @Override
        protected String doInBackground(Void... voids) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] imgByte = byteArrayOutputStream.toByteArray();
            String imageBitmap = Base64.encodeToString(imgByte, Base64.DEFAULT);
            return imageBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakContext.get();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            convertBitmap.bitmapToString(s);
        }
    }
}