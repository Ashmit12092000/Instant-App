package com.project.tesla.Project.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tesla.Project.Activity.ChatActivity;
import com.project.tesla.Project.Activity.ShareActivity;
import com.project.tesla.Project.Cryptography.EncodeMessages;
import com.project.tesla.Project.Model.Message;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;
import com.project.tesla.databinding.SharelistlayoutBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ShareListAdapters extends RecyclerView.Adapter<ShareListAdapters.ShareViewHolder> {
    Context context;
    ArrayList<User> userList;

    public ShareListAdapters(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ShareListAdapters.ShareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sharelistlayout,parent,false);
        return new ShareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareListAdapters.ShareViewHolder holder, int position) {
        holder.setData(userList.get(position));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ShareViewHolder extends  RecyclerView.ViewHolder{

        SharelistlayoutBinding binding;
        public ShareViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=SharelistlayoutBinding.bind(itemView);
        }

        public void setData(User user) {
            Glide.with(context).load(user.getImage_uri()).into(binding.shareUserimage);
            binding.shareUsername.setText(user.getName());
            binding.shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ShareActivity)context).sharePost(user);
                    //.makeText(context, "Button Clicked", Toast.LENGTH_SHORT).show();

                }
            });


        }
    }
}
