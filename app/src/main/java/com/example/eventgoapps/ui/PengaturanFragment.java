package com.example.eventgoapps.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.eventgoapps.R;
import com.example.eventgoapps.databinding.FragmentPengaturanBinding;
import com.example.eventgoapps.util.Utils;

public class PengaturanFragment extends Fragment {

    FragmentPengaturanBinding binding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public PengaturanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pengaturan, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(Utils.LOGIN_KEY, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        boolean notif = sharedPreferences.getBoolean(Utils.NOTIF_TERDEKAT_STATUS, false);
        if (notif){
            binding.swNotifTerdekat.setChecked(true);
        }else {
            binding.swNotifTerdekat.setChecked(false);
        }

        binding.swNotifTerdekat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    editor.putBoolean(Utils.NOTIF_TERDEKAT_STATUS, true);
                    editor.commit();
                    Toast.makeText(getActivity(), "Notifikasi event terdekat di aktifkan", Toast.LENGTH_SHORT).show();
                }else {
                    editor.putBoolean(Utils.NOTIF_TERDEKAT_STATUS, false);
                    editor.commit();
                    Toast.makeText(getActivity(), "Notifikasi event terdekat di non aktifkan", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

    }
}