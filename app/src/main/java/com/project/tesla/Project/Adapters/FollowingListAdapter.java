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
import com.google.firebase.auth.FirebaseAuth;

import com.project.tesla.Project.Activity.UserProfileActivity;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;
import com.project.tesla.databinding.FollowerslistBinding;
import com.project.tesla.databinding.FollowinglistBinding;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class FollowingListAdapter extends RecyclerView.Adapter<FollowingListAdapter.FollowingViewholder> {
    Context context;
    ArrayList<User> users;
    FirebaseAuth mauth;

    public FollowingListAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public FollowingListAdapter.FollowingViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.followinglist,parent,false);
        return new FollowingViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowingListAdapter.FollowingViewholder holder, int position) {
        User user=users.get(position);
        holder.binding.followingname.setText(user.getName());
        Glide.with(context).load(user.getImage_uri()).placeholder(R.drawable.user).into(holder.binding.followingimage);
        holder.binding.followingimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserProfileActivity.class);
                intent.putExtra("uid",user.getUid());
                context.startActivity(intent);
            }
        });
        holder.binding.followingname.setOnClickListener(new View.OnClickListener() {
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


    public class FollowingViewholder extends  RecyclerView.ViewHolder {
        ImageView followingimage;
        TextView followingname;
        FollowinglistBinding binding;
        public FollowingViewholder(@NonNull View itemView) {
            super(itemView);
            binding= FollowinglistBinding.bind(itemView);
        }
    }
}
