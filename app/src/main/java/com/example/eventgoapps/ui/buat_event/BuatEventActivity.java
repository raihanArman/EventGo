package com.example.eventgoapps.ui.buat_event;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.ApiRequest;
import com.example.eventgoapps.databinding.ActivityBuatEventBinding;
import com.example.eventgoapps.util.Utils;

public class BuatEventActivity extends AppCompatActivity {

    int type_intent;
    public static boolean prosesInputEventDeskripsi = false;
    public static boolean statusLampiran = false;
    ApiRequest apiRequest;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String idUser;
    ActivityBuatEventBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_buat_event);
        type_intent = getIntent().getIntExtra("type_intent", 0);

        if (type_intent == Utils.TYPE_ADD){
            binding.setTitle("Buat Event");
            setFragment(new FormEventFragment());
        }else if (type_intent == Utils.TYPE_EDIT){
            binding.setTitle("Edit Event");
            String idEvent = getIntent().getStringExtra("id_event");
            Bundle bundle = new Bundle();
            bundle.putString("id_event", idEvent);
            FormEventEditFragment formEventEditFragment = new FormEventEditFragment();
            formEventEditFragment.setArguments(bundle);
            setFragment(formEventEditFragment);
        }

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!prosesInputEventDeskripsi){
                    finish();
                }else {
                    FormEventFragment.BACK_FROM_LAMPIRAN = true;
                    if (statusLampiran){
//                        if (lampiranList.size() > 0){
//                            lampiranHelper.cleanLampiran(idUser);
//                        }
                    }
                    getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });

    }

    private void setFragment(Fragment fragment){
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.replace(binding.frameUsulan.getId(), fragment);
        transaction.commit();

    }

}