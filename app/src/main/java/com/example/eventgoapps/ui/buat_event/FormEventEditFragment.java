package com.example.eventgoapps.ui.buat_event;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.data.remote.model.Kategori;
import com.example.eventgoapps.data.remote.model.response.EventResponse;
import com.example.eventgoapps.data.remote.model.response.KategoriResponse;
import com.example.eventgoapps.databinding.ActivityDetailEventUserBinding;
import com.example.eventgoapps.databinding.FragmentFormEventEditBinding;
import com.example.eventgoapps.util.ConvertBitmap;
import com.example.eventgoapps.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.example.eventgoapps.ui.buat_event.FormEventFragment.BACK_FROM_LAMPIRAN;
import static com.example.eventgoapps.ui.buat_event.FormEventFragment.GALLERY_CODE;

public class FormEventEditFragment extends Fragment implements ConvertBitmap{

    private static final String TAG = "FormEventEditFragment";

    FragmentFormEventEditBinding binding;
    String idEvent, idUser;
    ApiRequest apiRequest;

    boolean hasJam = false, hasTanggal = false, hasPamflet = false;
    Bitmap bitmap;
    int kategoriPosition, jenisKajianPosition;
    ProgressDialog progressDialog;
    String pamflet = "", jamResume, tanggalResume, tanggalInput;

    List<String> listIdKategori = new ArrayList<>();
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat simpleDateFormat;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form_event_edit, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idEvent = getArguments().getString("id_event");
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);
        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        idUser = sharedPreferences.getString(Utils.ID_USER_KEY, "");

        loadDataKategori();
        loadJenis();
        loadDataEvent();
        binding.lvJam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        binding.lvTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        binding.spKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kategoriPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.spJenisKajian.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jenisKajianPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.layoutBtnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_CODE);
            }
        });

        binding.tvGantiGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, GALLERY_CODE);
            }
        });


        binding.btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isConnectionInternet(getActivity())) {
                    cekInput();
                }else {
                    Toast.makeText(getActivity(), "Tidak ada jaringan", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void cekInput() {
        if(hasPamflet){
            if (!TextUtils.isEmpty(binding.etJudulKajian.getText().toString())){
                if(!TextUtils.isEmpty(binding.etSubJudul.getText().toString())){
                    if(!TextUtils.isEmpty(binding.etDeskripsi.getText().toString())){
                        if (!TextUtils.isEmpty(binding.etPemateri.getText().toString())){
                            if (hasJam){
                                if (hasTanggal){
                                    if (!TextUtils.isEmpty(binding.etLokasi.getText().toString())){
                                        if (!TextUtils.isEmpty(binding.etNoTelpPemateri.getText().toString())){
                                            goToLampiran(pamflet);
                                        }else {
                                            binding.etNoTelpPemateri.setError("No Panitia tidak boleh kosong");
                                            Utils.scrollToView(binding.svForm, binding.etNoTelpPemateri);
                                        }
                                    }else {
                                        binding.etLokasi.setError("Lokasi tidak boleh kosong");
                                        Utils.scrollToView(binding.svForm, binding.etLokasi);
                                    }
                                }else {
                                    Toast.makeText(getActivity(), "Anda belum memilih tanggal", Toast.LENGTH_LONG).show();
                                    binding.lvTanggal.setBackgroundColor(getResources().getColor(R.color.red_main));
                                    binding.tvTanggal.setTextColor(getResources().getColor(android.R.color.white));
                                    binding.ivTanggal.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                                    Utils.scrollToView(binding.svForm, binding.tvPilihTanggal);
                                }
                            }else {
                                Toast.makeText(getActivity(), "Anda belum memilih jam", Toast.LENGTH_LONG).show();
                                binding.lvJam.setBackgroundColor(getResources().getColor(R.color.red_main));
                                binding.tvJam.setTextColor(getResources().getColor(android.R.color.white));
                                binding.ivJam.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                                Utils.scrollToView(binding.svForm, binding.tvPilihTanggal);
                            }
                        }else {
                            binding.etPemateri.setError("Nama Panitia tidak boleh kosong");
                            Utils.scrollToView(binding.svForm, binding.etPemateri);
                        }
                    }else {
                        binding.etNoTelpPemateri.setError("Deskripsi tidak boleh kosong");
                        Utils.scrollToView(binding.svForm, binding.etDeskripsi);
                    }
                }else {
                    binding.etNoTelpPemateri.setError("Sub judul tidak boleh kosong");
                    Utils.scrollToView(binding.svForm, binding.etSubJudul);
                }


            }else {
                binding.etJudulKajian.setError("Judul kajian tidak boleh error");
                Utils.scrollToView(binding.svForm, binding.etJudulKajian);
            }
        }else {
            Toast.makeText(getActivity(), "Anda belum memasukkan pamflet", Toast.LENGTH_LONG).show();
            Utils.scrollToView(binding.svForm, binding.tvPamflet);
        }
    }

    private void goToLampiran(String pamflet) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Proses...");
        progressDialog.show();
        Event event = new Event();
        event.setIdEvent(idEvent);
        event.setJenis(binding.spJenisKajian.getAdapter().getItem(jenisKajianPosition).toString());
        event.setIdKategori(listIdKategori.get(kategoriPosition));
        event.setJudul(binding.etJudulKajian.getText().toString());
        event.setSubJudul(binding.etSubJudul.getText().toString());
        event.setPamflet(pamflet);
        event.setDeskripsi(binding.etDeskripsi.getText().toString());
        event.setNamaPanitia(binding.etPemateri.getText().toString());
        event.setLokasi(binding.etLokasi.getText().toString());
        event.setNoHpPanitia(binding.etNoTelpPemateri.getText().toString());
        event.setIdUser(idUser);
        String tanggal = tanggalInput+" "+binding.tvJam.getText().toString()+":00";
        event.setTanggal(tanggal);

        Calendar calendar = Calendar.getInstance();
        String tglUpdate = DateFormat.format("yyyy-MM-dd HH:mm:ss", calendar.getTime()).toString();
        event.setTglUpdate(tglUpdate);

        FormLampiranEditFragment lampiranFragment = new FormLampiranEditFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("event", event);
        bundle.putString("id_user", idUser);
        lampiranFragment.setArguments(bundle);

        progressDialog.dismiss();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_usulan, lampiranFragment)
                .addToBackStack("")
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE){
            if (resultCode == RESULT_OK && data != null){
                hasPamflet = true;
                Uri imageUri = data.getData();
                binding.ivKajian.setVisibility(View.VISIBLE);
                binding.layoutBtnAddImage.setVisibility(View.INVISIBLE);
                binding.tvGantiGambar.setVisibility(View.VISIBLE);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new LoadBitmapConvertAsync(getActivity(), FormEventEditFragment.this::bitmapToString).execute();
                binding.ivKajian.setImageBitmap(bitmap);

            }
        }
    }

    @Override
    public void bitmapToString(String imgConvert) {
//        Toast.makeText(getActivity(), ""+imgConvert, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        pamflet = imgConvert;
        Log.d(TAG, "bitmapToString: "+imgConvert);
    }

    private void loadJenis() {
        List<String> jenis = new ArrayList<>();
        jenis.add("Gratis");
        jenis.add("Berbayar");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, R.id.weekofday, jenis);
        binding.spJenisKajian.setAdapter(arrayAdapter);
    }

    private void loadDataEvent() {
        Call<EventResponse> eventResponseCall = apiRequest.aktivitasByIdRequest(idEvent);
        eventResponseCall.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.body().getValue() == 1){
                    Event event = response.body().getEventList().get(0);
                    binding.setEvent(event);

                    String tanggal = DateFormat.format("EEEE, dd MMM yyyy", event.getTglEvent()).toString();
                    String jam = DateFormat.format("HH:mm", event.getTglEvent()).toString();
                    binding.lvTanggal.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    binding.tvTanggal.setTextColor(getResources().getColor(android.R.color.white));
                    binding.ivTanggal.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                    binding.tvTanggal.setText(tanggal);


                    binding.tvJam.setText(jam);
                    binding.lvJam.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    binding.tvJam.setTextColor(getResources().getColor(android.R.color.white));
                    binding.ivJam.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));

                    binding.spKategori.setSelection(getIndex(binding.spKategori, event.getNamaKategori()));
                    binding.spJenisKajian.setSelection(getIndex(binding.spJenisKajian, event.getJenis()));

                    tanggalInput = DateFormat.format("yyyy-MM-dd", event.getTglEvent()).toString();

                    hasJam = true;
                    hasTanggal = true;
                    hasPamflet = true;

                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {

            }
        });
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        Toast.makeText(getActivity(), "Index : "+index, Toast.LENGTH_SHORT).show();
        return index;
    }

    private void loadDataKategori() {
        List<String> listNamaKategori = new ArrayList<>();
        Call<KategoriResponse> callAllKategori = apiRequest.kategoriAllRequest();
        callAllKategori.enqueue(new Callback<KategoriResponse>() {
            @Override
            public void onResponse(Call<KategoriResponse> call, Response<KategoriResponse> response) {
                List<Kategori> kategoriList = response.body().getKategoriList();
                for (int i=0; i<kategoriList.size(); i++){
                    listNamaKategori.add(kategoriList.get(i).getNamaKategori());
                    listIdKategori.add(kategoriList.get(i).getIdKategori());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_spinner, R.id.weekofday, listNamaKategori);
                binding.spKategori.setAdapter(arrayAdapter);

            }

            @Override
            public void onFailure(Call<KategoriResponse> call, Throwable t) {

            }
        });
    }

    private void showDatePicker() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                String hari = DateFormat.format("EEEE", newDate).toString();
                String tanggal = DateFormat.format("d MMMM yyyy", newDate).toString();
                tanggalInput = DateFormat.format("yyyy-MM-dd", newDate).toString();
                binding.tvTanggal.setText(hari+", "+tanggal);
                tanggalResume = simpleDateFormat.format(newDate.getTime());
                binding.lvTanggal.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                binding.tvTanggal.setTextColor(getResources().getColor(android.R.color.white));
                binding.ivTanggal.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                hasTanggal = true;
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hourNormalisasi = "00", minuteNormalisasi = "00";
                if (hourOfDay < 10){
                    String value = String.valueOf(hourOfDay);
                    hourNormalisasi = "0"+value;
                }else {
                    hourNormalisasi = String.valueOf(hourOfDay);
                }
                if (minute < 10){
                    String value = String.valueOf(minute);
                    minuteNormalisasi = "0"+value;
                }else {
                    minuteNormalisasi = String.valueOf(minute);
                }
                binding.tvJam.setText(hourNormalisasi+":"+minuteNormalisasi);
                binding.lvJam.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                binding.tvJam.setTextColor(getResources().getColor(android.R.color.white));
                binding.ivJam.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
                jamResume = hourNormalisasi+":"+minuteNormalisasi;
                hasJam = true;
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));

        timePickerDialog.show();

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

    @Override
    public void onResume() {
        super.onResume();
        if (BACK_FROM_LAMPIRAN) {
            binding.ivKajian.setVisibility(View.VISIBLE);
            binding.layoutBtnAddImage.setVisibility(View.INVISIBLE);
            binding.tvGantiGambar.setVisibility(View.VISIBLE);
            binding.ivKajian.setImageBitmap(bitmap);

            binding.tvJam.setText(jamResume);
            binding.lvJam.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            binding.tvTanggal.setText(tanggalResume);
            binding.lvTanggal.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

}