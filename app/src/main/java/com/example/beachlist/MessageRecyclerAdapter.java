package com.example.beachlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;

import java.util.List;

public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MyViewHolder> {
    Context context;
    List<DataSnapshot> list;

    public MessageRecyclerAdapter(Context context, List<DataSnapshot> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MessageRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_screen_cardview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageRecyclerAdapter.MyViewHolder holder, final int position) {
        Glide.with(context)
                .load(list.get(position).child("data").getValue(UserData.class).getImageUrl())
                .centerCrop()
                .into(holder.userPic);

        String fullName = (list.get(position).child("data").getValue(UserData.class).getFirstName()
                +" "+list.get(position).child("data").getValue(UserData.class).getLastName());

        holder.userName.setText(fullName);

        // when a listing is clicked, the position is taken to get that listings info
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SelectedUser.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView userPic;
        TextView userName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userPic = itemView.findViewById(R.id.user_pic);
            userName = itemView.findViewById(R.id.message_user_full_name);
        }
    }
}