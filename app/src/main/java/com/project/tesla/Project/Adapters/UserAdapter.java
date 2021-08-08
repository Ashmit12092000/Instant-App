package com.project.tesla.Project.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.project.tesla.Project.Activity.ChatActivity;
import com.project.tesla.Project.Cryptography.DecodingMessages;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;
import com.project.tesla.databinding.ProfileclickdialogBinding;
import com.project.tesla.databinding.SampleUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UsersViewHolder>{
    Context context;

    ArrayList<User> users;
    DatabaseReference chatinforef=FirebaseDatabase.getInstance().getReference().child("chat_info");
    public static String name;
    public UserAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_user,parent,false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);
        holder.binding.username.setText(user.getName());
        Picasso.get().load(user.getProfile_image())
                .placeholder(R.drawable.user)
                .into(holder.binding.profileImage);
        String sender_id= FirebaseAuth.getInstance().getUid();
        String sender_Room= sender_id+user.getUid();
        String recieverRoom= user.getUid()+sender_id;
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(sender_Room)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                            Long tim = snapshot.child("lastMsgTime").getValue(Long.class);

                            SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm a");
                            holder.binding.time.setText(dateFormat.format(new Date(tim)));
                            holder.binding.lastMessage.setVisibility(View.VISIBLE);
                            if (lastMsg.equals("photo") || lastMsg.equals("video") || lastMsg.equals("pdf") || lastMsg.equals("camera"))
                            {
                                holder.binding.lastMessage.setText(lastMsg);
                            }
                            else {
                                holder.binding.lastMessage.setText(DecodingMessages.decode(lastMsg));

                            }


                        }
                        else {
                            assert holder.binding.lastMessage2 != null;
                            holder.binding.lastMessage.setVisibility(View.GONE);
                            holder.binding.lastMessage2.setVisibility(View.VISIBLE); }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.binding.chatLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name",user.getName());
                name=user.getName();
                intent.putExtra("image",user.getProfile_image());
                intent.putExtra("uid",user.getUid());
                context.startActivity(intent);
            }
        });
        holder.binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v = LayoutInflater.from(context).inflate(R.layout.profileclickdialog, null);
                ProfileclickdialogBinding binding = ProfileclickdialogBinding.bind(v);
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setView(binding.getRoot())
                        .setCancelable(true)
                        .create();
                try {
                    
                    Glide.with(context).load(user.getProfile_image())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    binding.profileProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    binding.profileProgressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .into(binding.profileDiagImage);
                    
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                dialog.show();

            }
        });
       /* FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(recieverRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        chatinforef.child(FirebaseAuth.getInstance().getUid())
                                .child(user.getUid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            String unseen_msg_count=snapshot.child("unseen_msg_count").getValue(String.class);
                                            int unseen_count=Integer.parseInt(unseen_msg_count);
                                            if(unseen_msg_count==null) {
                                                Toast.makeText(context, "count is null", Toast.LENGTH_SHORT).show();
                                            }
                                            else if(unseen_count>0 ){
                                                holder.binding.unseenMsgCount.setVisibility(View.VISIBLE);
                                                holder.binding.unseenMsgCount.setText(unseen_msg_count);
                                            }
                                            else{
                                                holder.binding.unseenMsgCount.setVisibility(View.GONE);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });*/


    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void filter(ArrayList<User> filteredList) {
        users=filteredList;
        notifyDataSetChanged();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        SampleUserBinding binding;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleUserBinding.bind(itemView);
        }
    }
}
