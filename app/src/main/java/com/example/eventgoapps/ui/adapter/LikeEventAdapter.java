package com.example.eventgoapps.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.model.User;
import com.example.eventgoapps.databinding.ItemEventLikeBinding;

import java.util.ArrayList;
import java.util.List;

public class LikeEventAdapter extends RecyclerView.Adapter<LikeEventAdapter.ViewHolder> {
    List<User> userList = new ArrayList<>();
    Context context;

    public LikeEventAdapter(Context context) {
        this.context = context;
    }

    public void setUserList(List<User> userList){
        this.userList.clear();
        this.userList.addAll(userList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LikeEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEventLikeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_event_like,parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeEventAdapter.ViewHolder holder, int position) {
        if (userList.size() > 0) {
            holder.binding.setUser(userList.get(position));
            if (userList.size() > 4) {
                if (position == 3) {
                    holder.binding.relative.setVisibility(View.VISIBLE);
                    holder.binding.tvCount.setText(String.valueOf(userList.size() - 4));
                } else {
                    holder.binding.relative.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemEventLikeBinding binding;
        public ViewHolder(@NonNull ItemEventLikeBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
