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
import com.example.eventgoapps.databinding.ItemKategoriBinding;
import com.example.eventgoapps.ui.KategoriMoreFragment;
import com.example.eventgoapps.ui.home.KategoriFragment;

import java.util.ArrayList;
import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.ViewHolder> {

    List<Kategori> kategoriList = new ArrayList<>();
    Context context;

    public KategoriAdapter(Context context) {
        this.context = context;
    }

    public void setKategoriList(List<Kategori> kategoriList){
        this.kategoriList.clear();
        this.kategoriList.addAll(kategoriList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KategoriAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemKategoriBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_kategori, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriAdapter.ViewHolder holder, int position) {
        String idKategori = "";
        if(position <= 3) {
            idKategori = kategoriList.get(position).getIdKategori();
            holder.binding.setKategori(kategoriList.get(position));
        }else {
            Kategori kategori = new Kategori();
            kategori.setGambar("more.png");
            kategori.setNamaKategori("Lainnya");
            idKategori = "0";
            holder.binding.setKategori(kategori);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout frameLayout = ((MainActivity)context).binding.frameMain;
                FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                String idKategori = "", title="";
                if(position <= 3) {
                    title = kategoriList.get(position).getNamaKategori();
                    idKategori = kategoriList.get(position).getIdKategori();

                    KategoriFragment kategoriFragment = new KategoriFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id_kategori", idKategori);
                    bundle.putString("title", title);
                    kategoriFragment.setArguments(bundle);

                    transaction.replace(frameLayout.getId(), kategoriFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }else {
                    transaction.replace(frameLayout.getId(), new KategoriMoreFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(kategoriList.size() > 4)
            return 5;
        else
            return kategoriList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemKategoriBinding binding;
        public ViewHolder(@NonNull ItemKategoriBinding itemView) {
            super(itemView.getRoot());
            this.binding =itemView;
        }
    }
}
