package com.project.tesla.Project.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.project.tesla.Project.Model.Comments;
import com.project.tesla.R;
import com.project.tesla.databinding.CommentlayoutBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentsAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<Comments> commentsList;
    final int ITEM_SENT=1;
    final int ITEM_RECEIVED=2;
    DatabaseReference userRef;

    public CommentsAdapter(Context context, ArrayList<Comments> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SENT){
            View view= LayoutInflater.from(context).inflate(R.layout.commentlayout,parent,false);
            return new SenderViewHolder(view);
        }
        else{
            View view= LayoutInflater.from(context).inflate(R.layout.commentlayout,parent,false);
            return new RecieverViewHolder(view);
        }
    }
    @Override
    public int getItemViewType(int position) {
        Comments comment =commentsList.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(comment.getSenderID())){
            return  ITEM_SENT;
        }
        else {
            return ITEM_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comments comments=commentsList.get(position);
        userRef=FirebaseDatabase.getInstance().getReference().child("users");
        if(holder.getClass()==SenderViewHolder.class){
            SenderViewHolder viewHolder=(SenderViewHolder) holder;
            viewHolder.binding.commentMsg.setText(comments.getComment_msg());
            FirebaseDatabase.getInstance().getReference()
                    .child("Post_comments")
                    .child(comments.getPostID())
                    .child("comments")
                    .child(comments.getCommentsID())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                String senderID = snapshot.child("senderID").getValue(String.class);
                                userRef.child(senderID).child("name")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                if(snapshot.exists()) {
                                                    String username = snapshot1.getValue(String.class);
                                                    viewHolder.binding.cUsername.setText("@ "+username);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            FirebaseDatabase.getInstance().getReference()
                    .child("Post_comments")
                    .child(comments.getPostID())
                    .child("comments")
                    .child(comments.getCommentsID())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                Long tim = snapshot.child("timestamp").getValue(Long.class);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                viewHolder.binding.cTime.setText(dateFormat.format(new Date(tim)));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
        else{
            RecieverViewHolder viewHolder=(RecieverViewHolder) holder;
            viewHolder.binding.commentMsg.setText(comments.getComment_msg());
            FirebaseDatabase.getInstance().getReference()
                    .child("Post_comments")
                    .child(comments.getPostID())
                    .child("comments")
                    .child(comments.getCommentsID())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                String senderID = snapshot.child("senderID").getValue(String.class);
                                userRef.child(senderID).child("name")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                                if(snapshot.exists()) {
                                                    String username = snapshot1.getValue(String.class);
                                                    viewHolder.binding.cUsername.setText("@ "+username);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            FirebaseDatabase.getInstance().getReference()
                    .child("Post_comments")
                    .child(comments.getPostID())
                    .child("comments")
                    .child(comments.getCommentsID())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                Long tim = snapshot.child("timestamp").getValue(Long.class);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                viewHolder.binding.cTime.setText(dateFormat.format(new Date(tim)));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }
    public  class SenderViewHolder extends RecyclerView.ViewHolder{
        CommentlayoutBinding binding;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=CommentlayoutBinding.bind(itemView);
        }
    }
    public  class RecieverViewHolder extends RecyclerView.ViewHolder{
        CommentlayoutBinding binding;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=CommentlayoutBinding.bind(itemView);
        }
    }
}
