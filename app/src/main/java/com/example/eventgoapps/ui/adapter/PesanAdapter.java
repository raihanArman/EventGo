package com.example.eventgoapps.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.model.Pesan;
import com.example.eventgoapps.databinding.ItemPesanBinding;
import com.example.eventgoapps.ui.pesan.PesanDetailFragmentDialog;
import com.example.eventgoapps.util.TimeStampFormatter;

import java.util.ArrayList;
import java.util.List;

public class PesanAdapter extends RecyclerView.Adapter<PesanAdapter.ViewHolder> {
    List<Pesan> pesanList = new ArrayList<>();
    Context context;
    TimeStampFormatter timeStampFormatter;

    public PesanAdapter(Context context, TimeStampFormatter timeStampFormatter) {
        this.context = context;
        this.timeStampFormatter = timeStampFormatter;
    }

    public void setPesanList(List<Pesan> pesanList){
        this.pesanList.clear();
        this.pesanList.addAll(pesanList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PesanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPesanBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_pesan, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PesanAdapter.ViewHolder holder, int position) {
        Pesan pesan = pesanList.get(position);
        holder.binding.setPesan(pesan);
        if (pesan.getStatus().equals("baca")){
            holder.binding.vStatus.setVisibility(View.GONE);
        }

        holder.binding.setTanggal(timeStampFormatter.format(pesan.getTanggal()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = ((FragmentActivity)context).getSupportFragmentManager();
                PesanDetailFragmentDialog detailFragmentDialog = new PesanDetailFragmentDialog(pesan.getIdPesanUser());
                detailFragmentDialog.show(fm, "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return pesanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemPesanBinding binding;
        public ViewHolder(@NonNull ItemPesanBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
