package com.example.eventgoapps.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.databinding.ItemEventUserBinding;
import com.example.eventgoapps.ui.DetailEventActivity;
import com.example.eventgoapps.ui.DetailEventUserActivity;
import com.example.eventgoapps.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class AktivitasAdapter extends RecyclerView.Adapter<AktivitasAdapter.ViewHolder> {
    List<Event> eventResponseList = new ArrayList<>();
    Context context;

    public AktivitasAdapter(Context context) {
        this.context = context;
    }

    public void setEventResponseList(List<Event> eventResponseList){
        this.eventResponseList.clear();
        this.eventResponseList.addAll(eventResponseList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AktivitasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventUserBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_user, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AktivitasAdapter.ViewHolder holder, int position) {
        Event event = eventResponseList.get(position);
        String tanggal = DateFormat.format("EEEE, dd MMM yyyy", event.getTglEvent()).toString();
        String jam = DateFormat.format("HH:mm", event.getTglEvent()).toString();
        holder.binding.setJam(jam);
        holder.binding.setTanggal(tanggal);
        holder.binding.setEvent(event);

        if (event.getStatus().equals("ditolak")){
            holder.binding.lvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.red_main));
            holder.binding.tvStatus.setText("Di tolak");
        }else if(event.getStatus().equals("diterima")){
            holder.binding.lvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.success));
            holder.binding.tvStatus.setText("Di terima");
        }else if(event.getStatus().equals("sedang proses")){
            holder.binding.lvStatus.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.proses));
            holder.binding.tvStatus.setText("sedang proses");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Proses ...");
                progressDialog.show();
                Intent intent = new Intent(context, DetailEventUserActivity.class);
                intent.putExtra("type_intent", Utils.TYPE_INTENT_AKTIVITAS);
                intent.putExtra("id_event", event.getIdEvent());
                context.startActivity(intent);
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventResponseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemEventUserBinding binding;
        public ViewHolder(@NonNull ItemEventUserBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
