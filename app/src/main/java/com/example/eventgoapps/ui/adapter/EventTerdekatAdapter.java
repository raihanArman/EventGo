package com.example.eventgoapps.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.databinding.ItemTerbaruBinding;
import com.example.eventgoapps.databinding.ItemTerdekatBinding;
import com.example.eventgoapps.ui.DetailEventActivity;

import java.util.ArrayList;
import java.util.List;

public class EventTerdekatAdapter extends RecyclerView.Adapter<EventTerdekatAdapter.ViewHolder> {
    List<Event> eventList = new ArrayList<>();
    Context context;

    public EventTerdekatAdapter(Context context) {
        this.context = context;
    }

    public void setEventList(List<Event> eventList){
        this.eventList.clear();
        this.eventList.addAll(eventList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventTerdekatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTerdekatBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_terdekat, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventTerdekatAdapter.ViewHolder holder, int position) {
        Event event = eventList.get(position);
        String tanggal = DateFormat.format("EEEE, dd MMM yyyy", event.getTglEvent()).toString();
        String jam = DateFormat.format("HH:mm", event.getTglEvent()).toString();
        holder.binding.setJam(jam);
        holder.binding.setTanggal(tanggal);
        holder.binding.setEvent(event);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Proses ...");
                progressDialog.show();
                Intent intent = new Intent(context, DetailEventActivity.class);
                intent.putExtra("id_event", event.getIdEvent());
                context.startActivity(intent);
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemTerdekatBinding binding;
        public ViewHolder(@NonNull ItemTerdekatBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
