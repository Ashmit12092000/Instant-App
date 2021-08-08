package com.project.tesla.Project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tesla.Project.Adapters.ShareListAdapters;
import com.project.tesla.Project.Model.Message;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ShareActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ShareListAdapters adapter;
    ArrayList<User> usersList;
    FirebaseAuth mauth;
    FirebaseDatabase db;
    String sender_uid;
    Toolbar toolbar;
    String senderRoom;
    String recieverRoom;
    String postID;
    String postType;
    String receiver_uid;
    String imageUri;
    String video_uri;
    DatabaseReference userRef;
    DatabaseReference chatinfoRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mauth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.shareRecyclerView);
        toolbar=findViewById(R.id.shareToolbar);
        usersList = new ArrayList<>();
        sender_uid=mauth.getUid();

        postID=getIntent().getStringExtra("postID");
        imageUri=getIntent().getStringExtra("image_uri");
        postType=getIntent().getStringExtra("postType");
        video_uri=getIntent().getStringExtra("video_uri");

        chatinfoRef=FirebaseDatabase.getInstance().getReference().child("chat_info");
        adapter = new ShareListAdapters(ShareActivity.this, usersList);
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("users");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    User usr = snapshot1.getValue(User.class);
                    try {
                        usersList.add(usr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sharePost(User user) {
        try{
            Date date = new Date();
            Message message = new Message(sender_uid, "", date.getTime(), receiver_uid);
            //message.setMessage("message");
            if(postType.equals("video")){
                message.setMessage("video");
                message.setImage_uri(video_uri);
            }
            else {
                message.setMessage("post");
                message.setImage_uri(imageUri);
            }
            receiver_uid=user.getUid();
            senderRoom = sender_uid + receiver_uid;
            recieverRoom = receiver_uid + sender_uid;
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            String Key = db.getReference().push().getKey();

            HashMap<String, Object> lastMsgObj = new HashMap<>();
            lastMsgObj.put("lastMsg", message.getMessage());
            lastMsgObj.put("lastMsgTime", date.getTime());
            db.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
            db.getReference().child("chats").child(recieverRoom).updateChildren(lastMsgObj);
            assert Key != null;
            db.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(Key)
                    .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    db.getReference().child("chats")
                            .child(recieverRoom)
                            .child("messages")
                            .child(Key)
                            .setValue(message)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                    try {
                        Intent intent = new Intent(ShareActivity.this,ChatActivity.class);
                        intent.putExtra("name",user.getName());
                        intent.putExtra("image",user.getProfile_image());
                        intent.putExtra("uid",user.getUid());
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            chatinfoRef.child(sender_uid).child(receiver_uid).child("last_message").setValue(message);
            chatinfoRef.child(receiver_uid).child(sender_uid).child("last_message").setValue(message);
            chatinfoRef.child(sender_uid).child(receiver_uid).child("Msg_time").setValue(date.getTime());
            chatinfoRef.child(receiver_uid).child(sender_uid).child("Msg_time").setValue(date.getTime());
            chatinfoRef.child(sender_uid).child(receiver_uid).child("msg_seen").setValue("false");
            chatinfoRef.child(sender_uid).child(receiver_uid)
                    .child("unseen_msg_count")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                chatinfoRef.child(sender_uid).child(receiver_uid).child("unseen_msg_count").setValue("1");
                            } else {
                                String total = snapshot.getValue().toString();
                                int temp = Integer.parseInt(total) + 1;
                                chatinfoRef.child(sender_uid).child(receiver_uid).child("unseen_msg_count").setValue(String.valueOf(temp));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}