package com.project.tesla.Project.Adapters;

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
import com.project.tesla.Project.Activity.UserProfileActivity;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;
import com.project.tesla.databinding.FollowerslistBinding;


import java.util.ArrayList;

public class FollowersListAdapter extends RecyclerView.Adapter<FollowersListAdapter.FollowersViewholder> {
    Context context;
    ArrayList<User> users;

    public FollowersListAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public FollowersListAdapter.FollowersViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.followerslist,parent,false);
        return new FollowersViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersListAdapter.FollowersViewholder holder, int position) {
        User user=users.get(position);

        holder.binding.follwersname.setText(user.getName());
        Glide.with(context).load(user.getImage_uri()).placeholder(R.drawable.user).into(holder.binding.followerimage);

        holder.binding.follwersname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserProfileActivity.class);
                intent.putExtra("uid",user.getUid());
                context.startActivity(intent);
            }
        });
        holder.binding.followerimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserProfileActivity.class);
                intent.putExtra("uid",user.getUid());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class FollowersViewholder extends  RecyclerView.ViewHolder{
        FollowerslistBinding binding;
        public FollowersViewholder(@NonNull View itemView) {
            super(itemView);
            binding=FollowerslistBinding.bind(itemView);
        }

    }
}
