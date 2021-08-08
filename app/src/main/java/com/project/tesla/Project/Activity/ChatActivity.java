package com.project.tesla.Project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.project.tesla.Project.Adapters.MessageAdapter;
import com.project.tesla.Project.Cryptography.EncodeMessages;
import com.project.tesla.Project.Model.Message;
import com.project.tesla.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    ImageView send_btn;
    EditText chat_message;
    RecyclerView recyclerView;
    ImageView camera;
    TextView last_Seen_time;
    ScrollView scrollView;
    DatabaseReference ref,chatinfoRef;
    Lottiedialog lottiedialog;
    ArrayList<Message> messages;
    FirebaseStorage storage;
    FirebaseAuth mauth;
    ValueEventListener listener;
    String receiver_uid;
    private static  String uid;
    String recieverRoom;
    MessageAdapter messageAdapter;
    String sender_name;
    TextView seen;
    String displayName;
    TextView name_user;
    TextView status_user;
    ImageView profile_pic;
    Toolbar toolbar;
    FirebaseDatabase db;
    String sender_uid;
    ProgressDialog dialog;
    ProgressDialog loadingBar;
    ImageView back;
    ImageView attachment;
    String senderRoom;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mauth=FirebaseAuth.getInstance();
        chatinfoRef= FirebaseDatabase.getInstance().getReference().child("chat_info");
        db=FirebaseDatabase.getInstance();
        chat_message=findViewById(R.id.chat_message);
        profile_pic= findViewById(R.id.profile_user);
        loadingBar = new ProgressDialog(ChatActivity.this);
        recyclerView=findViewById(R.id.chat_recycler_Views);
        sender_uid= FirebaseAuth.getInstance().getUid();
        receiver_uid = getIntent().getStringExtra("uid");
        uid=receiver_uid;
        senderRoom=sender_uid+ receiver_uid;
        ref=FirebaseDatabase.getInstance().getReference().child("users");
        recieverRoom= receiver_uid + sender_uid;
        camera= findViewById(R.id.camera);
        storage=FirebaseStorage.getInstance();
        last_Seen_time=findViewById(R.id.last_seen);
        lottiedialog=new Lottiedialog(ChatActivity.this);
        send_btn=findViewById(R.id.send);
        toolbar= findViewById(R.id.chattoolbar);
        back=findViewById(R.id.cback);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        name_user= findViewById(R.id.name_user);
        final Handler handler = new Handler();
        chat_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Date date =new Date();
                db.getReference().child("status").child(sender_uid).setValue("Typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(StopTyping,1000);
            }


            final Runnable StopTyping=new Runnable() {
                @Override
                public void run() {
                    db.getReference().child("status").child(sender_uid).setValue("Online");
                }
            };
        });
        name_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatActivity.this,UserProfileActivity.class);
                intent.putExtra("uid",receiver_uid);
                startActivity(intent);
            }
        });
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ChatActivity.this,UserProfileActivity.class);
                intent.putExtra("uid",receiver_uid);
                startActivity(intent);
            }
        });
        attachment= findViewById(R.id.attachment);
        FirebaseDatabase.getInstance().getReference().child("users").child(mauth.getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sender_name=snapshot.child("display_name").getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        messages=new ArrayList<>();
        messageAdapter= new MessageAdapter(this,messages,senderRoom,recieverRoom);
        recyclerView.setAdapter(messageAdapter);
        name = getIntent().getStringExtra("name");
        String profile = getIntent().getStringExtra("image");

        name_user.setText(name);

        Picasso.get().load(profile)
                .placeholder(R.drawable.user)
                .into(profile_pic);
        db.getReference().child("status").child(receiver_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String status = snapshot.getValue(String.class);
                    if(!(status.isEmpty())){
                        status_user.setText(status);
                        last_Seen_time.setVisibility(View.GONE);
                        status_user.setVisibility(View.VISIBLE);
                    }
                    else{
                        status_user.setVisibility(View.GONE);
                        last_Seen_time.setVisibility(View.VISIBLE);
                        FirebaseDatabase.getInstance().getReference()
                                .child("users")
                                .child(receiver_uid)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()){
                                            Long last_Seen = snapshot.child("last_Seen").getValue(Long.class);
                                            if (last_Seen!=null){
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                                last_Seen_time.setText("Last seen at: "+dateFormat.format(new Date(last_Seen)));
                                            }
                                            else {
                                                last_Seen_time.setVisibility(View.GONE);

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db.getReference()
                .child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageID(snapshot1.getKey());
                            messages.add(message);
                        }
                        messageAdapter.notifyDataSetChanged();
                        //recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message_text = chat_message.getText().toString();
                if(!(chat_message.getText().toString().equals(""))){
                    String encrpytedMessage = EncodeMessages.encode(message_text);
                    Date date = new Date();
                    Message message = new Message(sender_uid, encrpytedMessage, date.getTime(), receiver_uid);
                    //message.setMessage("message");
                    chat_message.setText("");
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
                }
                else{
                    Toast.makeText(ChatActivity.this, "Please write something", Toast.LENGTH_SHORT).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this,MessageActivity.class));
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        String currentuid=FirebaseAuth.getInstance().getUid();
        FirebaseDatabase database =FirebaseDatabase.getInstance();

        database.getReference().child("status").child(currentuid).setValue("Online");
        chatinfoRef.child(receiver_uid).child(sender_uid).child("msg_seen").setValue("true");
        chatinfoRef.child(receiver_uid).child(sender_uid).child("unseen_msg_count").setValue("0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatinfoRef.child(receiver_uid).child(sender_uid).child("msg_seen").setValue("true");
        chatinfoRef.child(receiver_uid).child(sender_uid).child("unseen_msg_count").setValue("0");
    }

    @Override
    protected void onPause() {
        super.onPause();
        chatinfoRef.child(receiver_uid).child(sender_uid).child("msg_seen").setValue("false");

    }

}