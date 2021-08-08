package com.project.tesla.Project.Activity;

import android.content.Intent;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.project.tesla.Project.Adapters.CommentsAdapter;
import com.project.tesla.Project.Model.Comments;
import com.project.tesla.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {
    FirebaseAuth mauth;
    FirebaseDatabase db;
    DatabaseReference postRef;
    DatabaseReference usersRef;
    ImageView post_img,userDP,send_btn;
    TextView username,time,caption,likes,new_comment_btn;
    EditText comment_msg;
    String post_username;
    String post_image_uri;
    ImageView likeimg;
    String post_time;
    String post_userDP;
    String senderID;
    String likescount,text_caption;
    String postID;
    DatabaseReference commentRef;
    String uid;
    RecyclerView recyclerView;
    CommentsAdapter adapter;
    ArrayList<Comments> commentsList;
    TextView Likescount;
    LinearLayout comment_headers_layout;
    LinearLayout new_comment_layout;
     DatabaseReference likeref;
    String postkey;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        FirebaseApp.initializeApp(this);
        mauth=FirebaseAuth.getInstance();
        Likescount=findViewById(R.id.likes_coount);
        likeimg=findViewById(R.id.likeimg);
        db=FirebaseDatabase.getInstance();
        toolbar=findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        post_image_uri=getIntent().getStringExtra("post_img");
        post_username=getIntent().getStringExtra("username");
        postID=getIntent().getStringExtra("postID");
        post_time=getIntent().getStringExtra("time");
        post_userDP=getIntent().getStringExtra("userDP");
        likescount=getIntent().getStringExtra("like_count");
        uid=getIntent().getStringExtra("uid");
        text_caption=getIntent().getStringExtra("caption");
        postRef=db.getReference().child("Posts").child(postID);
        commentRef=db.getReference().child("Post_comments").child(postID);
        usersRef=db.getReference().child("users");

        post_img=findViewById(R.id.post_img);
        username=findViewById(R.id.userName);
        time=findViewById(R.id.Timeofpost);
        caption=findViewById(R.id.postcaption);
        new_comment_btn=findViewById(R.id.new_comment_btn);
        comment_headers_layout=findViewById(R.id.comment_header_layout);

        new_comment_layout=findViewById(R.id.new_comment_layout);
        comment_headers_layout.setVisibility(View.VISIBLE);
        new_comment_layout.setVisibility(View.GONE);
        userDP=findViewById(R.id.cprofile_img);
        send_btn=findViewById(R.id.send_btn);
        comment_msg=findViewById(R.id.msg_box);
        Glide.with(CommentsActivity.this).load(post_image_uri).into(post_img);
        Glide.with(CommentsActivity.this).load(post_userDP).into(userDP);
        username.setText(post_username);
        time.setText(post_time);
        caption.setText(text_caption);

        recyclerView=findViewById(R.id.comment_recycler_view);
        commentsList=new ArrayList<>();
        adapter=new CommentsAdapter(CommentsActivity.this,commentsList);
        recyclerView.setAdapter(adapter);

        commentRef.child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Comments comment = snapshot1.getValue(Comments.class);
                    comment.setCommentsID(snapshot1.getKey());
                    commentsList.add(comment);
                }
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        new_comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_headers_layout.setVisibility(View.GONE);
                new_comment_layout.setVisibility(View.VISIBLE);
            }
        });
        getlikedStatus(postID);
        nrlikes(postID);
        likeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likeimg.getTag().equals("not_liked")){
                    FirebaseDatabase.getInstance().getReference().child("likes").child(postID)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("likes").child(postID)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue();
                }
            }
        });
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg_txt=comment_msg.getText().toString();
                Date date=new Date();
                senderID=mauth.getUid();
                Comments comment=new Comments(senderID,postID,msg_txt,date.getTime());
                comment_msg.setText("");

                try {
                    commentRef
                            .child("comments")
                            .push()
                            .setValue(comment)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(CommentsActivity.this, "Comment sent successfully..", Toast.LENGTH_SHORT).show();
                                }
                            });
                    HashMap<String,Object> last_MsgNtime=new HashMap<>();
                    last_MsgNtime.put("lastMessage",comment.getComment_msg());
                    last_MsgNtime.put("timestamp",comment.getTimestamp());
                    last_MsgNtime.put("senderID",comment.getSenderID());
                    commentRef.updateChildren(last_MsgNtime);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                comment_headers_layout.setVisibility(View.VISIBLE);
                new_comment_layout.setVisibility(View.GONE);
            }

        });

    }
    public void getlikedStatus(String postkey) {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        likeref=FirebaseDatabase.getInstance().getReference("likes").child(postkey);
        likeref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()) {

                    likeimg.setImageResource(R.drawable.ic_heart);
                    likeimg.setTag("liked");
                }
                else{

                    likeimg.setImageResource(R.drawable.ic_unlike);
                    likeimg.setTag("not_liked");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void nrlikes(String post_id) {
        DatabaseReference likesref=FirebaseDatabase.getInstance().getReference().child("likes")
                .child(post_id);
        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int likescount=(int)snapshot.getChildrenCount();
                Likescount.setText(likescount+" Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(CommentsActivity.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}