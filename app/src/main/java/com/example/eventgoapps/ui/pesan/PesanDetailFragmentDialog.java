package com.example.eventgoapps.ui.pesan;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.data.remote.RetrofitRequest;
import com.example.eventgoapps.data.remote.model.Pesan;
import com.example.eventgoapps.data.remote.model.Value;
import com.example.eventgoapps.data.remote.model.response.PesanResponse;
import com.example.eventgoapps.databinding.FragmentPesanDetailDialogBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PesanDetailFragmentDialog extends DialogFragment {
    String idPesan;
    FragmentPesanDetailDialogBinding binding;
    ApiRequest apiRequest;

    private static final String TAG = "PesanDetailFragmentDial";

    public PesanDetailFragmentDialog(String idPesan) {
        // Required empty public constructor
        this.idPesan = idPesan;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pesan_detail_dialog, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiRequest = RetrofitRequest.getInstance().create(ApiRequest.class);

        updatePesan();
        loadDataPesan();

        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        binding.lvHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusPesan();
            }
        });

    }

    private void hapusPesan() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pesan");
        builder.setMessage("Apakah anda yakin ingin menghapus pesan ini ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Call<Value> hapusCall = apiRequest.hapusPesanRequest(idPesan);
                hapusCall.enqueue(new Callback<Value>() {
                    @Override
                    public void onResponse(Call<Value> call, Response<Value> response) {
                        if (response.isSuccessful()){
                            Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().getValue() == 1) {
                                dialog.dismiss();
                                dismiss();
                            }else {
                                dialog.dismiss();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Value> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
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

    private void updatePesan() {
        Call<Value> updatePesanCall = apiRequest.updatePesanRequest(idPesan);
        updatePesanCall.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                if (response.isSuccessful()){
                    Log.d(TAG, "onResponse: Berhasil update");
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                Log.d(TAG, "onResponse: gagal update ,"+t.getMessage());
            }
        });
    }

    private void loadDataPesan() {
        Call<PesanResponse> pesanResponseCall = apiRequest.pesanByIdRequest(idPesan);
        pesanResponseCall.enqueue(new Callback<PesanResponse>() {
            @Override
            public void onResponse(Call<PesanResponse> call, Response<PesanResponse> response) {
                if (response.isSuccessful()){
                    if (response.body().getPesanList().size() > 0){
                        Pesan pesan = response.body().getPesanList().get(0);
                        binding.setPesan(pesan);
                        String tanggal = DateFormat.format("EEEE, dd MMM yyyy HH:mm", pesan.getTanggal()).toString();
                        binding.setTanggal(tanggal);
                    }
                }
            }

            @Override
            public void onFailure(Call<PesanResponse> call, Throwable t) {
                Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}