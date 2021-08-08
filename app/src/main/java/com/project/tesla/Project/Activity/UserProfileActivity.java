package com.project.tesla.Project.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mlsdev.animatedrv.AnimatedRecyclerView;
import com.project.tesla.Project.Adapters.UserGridPostsAdapter;
import com.project.tesla.Project.Fragments.Followersfragment;
import com.project.tesla.Project.Fragments.Followingfragment;
import com.project.tesla.Project.Model.Post;
import com.project.tesla.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    ImageView upload_prfile_img;
    Button follow_btn;
    Uri selected_image;
    TextView followers_count;
    TextView following_count;
    TextView post_count;
    TextView Posts;
    Toolbar toolbar;
    TextView Followers;
    TextView Following;
    DatabaseReference followersRef;
    AnimatedRecyclerView recyclerView;
    UserGridPostsAdapter adapter;
    ArrayList<Post> postList;
    FirebaseAuth mauth;
    FirebaseDatabase db;
    String uid;
    ProgressDialog dialog;
    String name;
    String image_uri;
    HashMap<String,Object> hashMap;
    HashMap<String,Object> following_hashMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_user_profile);
        mauth=FirebaseAuth.getInstance();
        uid=getIntent().getStringExtra("uid");
        toolbar=findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        upload_prfile_img=findViewById(R.id.user_upload_profile_img);
        followers_count=findViewById(R.id.user_followers_count);
        following_count=findViewById(R.id.user_following_count);
        post_count=findViewById(R.id.user_postcount);
        recyclerView=findViewById(R.id.user_post_recycler_view);
        Posts=findViewById(R.id.user_Posts);
        Followers=findViewById(R.id.user_Followers);
        Following=findViewById(R.id.user_Following);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        postList=new ArrayList<>();
        adapter=new UserGridPostsAdapter(UserProfileActivity.this,postList);
        recyclerView.setAdapter(adapter);
        db=FirebaseDatabase.getInstance();
        follow_btn=findViewById(R.id.user_follow_btn);
        dialog=new ProgressDialog(UserProfileActivity.this);
        hashMap =new HashMap<>();
        following_hashMap=new HashMap<>();
        dialog.setMessage("Loading...");
        db.getReference().child("users").child(mauth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name=snapshot.child("name").getValue(String.class);
                image_uri=snapshot.child("profile_image").getValue(String.class);
                hashMap.put("name",name);
                hashMap.put("profile_image",image_uri);
                hashMap.put("isfollower",true);
                hashMap.put("uid",mauth.getUid());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db.getReference().child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                name=snapshot.child("name").getValue(String.class);
                image_uri=snapshot.child("profile_image").getValue(String.class);

                Glide.with(UserProfileActivity.this).load(image_uri).placeholder(R.drawable.user).into(upload_prfile_img);
                following_hashMap.put("name",name);
                following_hashMap.put("profile_image",image_uri);
                following_hashMap.put("isfollowing",true);
                following_hashMap.put("uid",uid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (follow_btn.getTag().equals("not following")) {
                    db.getReference("followers").child(uid).child(mauth.getUid()).setValue(hashMap);
                    db.getReference("following").child(mauth.getUid()).child(uid).setValue(following_hashMap);
                }
                else{
                    db.getReference("followers").child(uid).child(mauth.getUid()).removeValue();
                    db.getReference("following").child(mauth.getUid()).child(uid).removeValue();
                }
            }
        });
        Followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("uid",uid);
                Followersfragment followersfragment=new Followersfragment();
                followersfragment.setArguments(bundle);
                Intent intent= new Intent(UserProfileActivity.this,FollowingInfo.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });
        Following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("uid",uid);
                Followingfragment followingfragment=new Followingfragment();
                followingfragment.setArguments(bundle);
                Intent intent= new Intent(UserProfileActivity.this,FollowingInfo.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }
        });
        FirebaseDatabase.getInstance().getReference()
                .child("PostCount")
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1:snapshot.getChildren()){
                            Post post=snapshot1.getValue(Post.class);
                            post.setPost_id(snapshot1.getKey());
                            postList.add(post);
                        }
                        adapter.notifyDataSetChanged();
                        recyclerView.scheduleLayoutAnimation();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        getFollowerstatus(uid,mauth.getUid());
        countfollowers(uid, mauth.getUid());
        countfollowing(uid,mauth.getUid());
        countPost(uid);

    }

    private void countPost(String uid) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("PostCount").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    int postcount=(int)snapshot.getChildrenCount();
                    post_count.setText(String.valueOf(postcount));
                }
                else{
                    post_count.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void countfollowing(String uid, String uid1) {
        DatabaseReference following=FirebaseDatabase.getInstance().getReference("following").child(uid);
        following.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    int followingcount=(int)snapshot.getChildrenCount();
                    following_count.setText(String.valueOf(followingcount));
                }
                else{
                    following_count.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getFollowerstatus(String uid, String myuid) {
        followersRef =FirebaseDatabase.getInstance().getReference("followers").child(uid);
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(mauth.getUid()).exists()){
                    follow_btn.setText(R.string.unfollow);
                    follow_btn.setTag("following");
                }
                else{
                    follow_btn.setText("Follow");
                    follow_btn.setTag("not following");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void countfollowers(String otheruid, String uid) {
        followersRef =FirebaseDatabase.getInstance().getReference("followers").child(otheruid);
        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    int followerscount=(int)snapshot.getChildrenCount();
                    followers_count.setText(String.valueOf(followerscount));
                }
                else{
                    followers_count.setText("0");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK && data.getData()!=null){
            dialog.show();
            upload_prfile_img.setImageURI(data.getData());
            selected_image = data.getData();
            StorageReference reference= FirebaseStorage.getInstance().getReference().child("Profile_Image").child(Objects.requireNonNull(mauth.getUid()));
            reference.putFile(selected_image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dialog.dismiss();
                                HashMap<String,Object> hashMap=new HashMap<>();
                                hashMap.put("profile_image",uri.toString());
                                FirebaseDatabase.getInstance().getReference()
                                        .child("users")
                                        .child(mauth.getUid())
                                        .updateChildren(hashMap);
                                }
                            });
                        }
                    }
                });
            }
        }

    }