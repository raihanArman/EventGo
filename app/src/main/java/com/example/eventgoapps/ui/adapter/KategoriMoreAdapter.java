package com.example.eventgoapps.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventgoapps.MainActivity;
import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.model.Kategori;
import com.example.eventgoapps.databinding.ItemSemuaKategoriBinding;
import com.example.eventgoapps.ui.home.KategoriFragment;

import java.util.ArrayList;
import java.util.List;

public class KategoriMoreAdapter extends RecyclerView.Adapter<KategoriMoreAdapter.ViewHolder> {

    List<Kategori> kategoriList = new ArrayList<>();
    Context context;

    public KategoriMoreAdapter(Context context) {
        this.context = context;
    }

    public void setKategoriList(List<Kategori> kategoriList){
        this.kategoriList.clear();
        this.kategoriList.addAll(kategoriList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KategoriMoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSemuaKategoriBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_semua_kategori, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriMoreAdapter.ViewHolder holder, int position) {
        holder.binding.setKategori(kategoriList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frameLayout = ((MainActivity)context).binding.frameMain;
                FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                String idKategori = "", title="";

                title = kategoriList.get(position).getNamaKategori();
                idKategori = kategoriList.get(position).getIdKategori();

                KategoriFragment kategoriFragment = new KategoriFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id_kategori", idKategori);
                bundle.putString("title", title);
                kategoriFragment.setArguments(bundle);

                transaction.replace(frameLayout.getId(), kategoriFragment);
                transaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemSemuaKategoriBinding binding;
        public ViewHolder(@NonNull ItemSemuaKategoriBinding itemView) {
            super(itemView.getRoot());
            this.binding =itemView;
        }
    }
}
