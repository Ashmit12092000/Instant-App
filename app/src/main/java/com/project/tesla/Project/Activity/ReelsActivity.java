package com.project.tesla.Project.Activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tesla.Project.Adapters.ReelsAdapter;
import com.project.tesla.Project.Model.Post;
import com.project.tesla.R;

import java.util.ArrayList;
import java.util.List;

public class ReelsActivity extends AppCompatActivity {
    List<Post> videoList=new ArrayList<>();
    ImageView add_reels_btn;
    ViewPager2 viewPager;
    ReelsAdapter adapter;
    ImageView back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_reels);
        FirebaseApp.initializeApp(this);
        add_reels_btn=findViewById(R.id.add_reels_btn);
        viewPager=findViewById(R.id.viewpager2);
        adapter=new ReelsAdapter(ReelsActivity.this,videoList);
        back_btn=findViewById(R.id.reel_back_btn);
        getSupportActionBar().hide();
        FirebaseDatabase.getInstance().getReference()
                .child("Reels")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        videoList.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Post post = snapshot1.getValue(Post.class);
                            post.setVid(snapshot1.getKey());
                            videoList.add(post);
                        }
                        viewPager.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        add_reels_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    startActivity(new Intent(ReelsActivity.this, AddReeels.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReelsActivity.this,MainActivity.class));

            }
        });
    }
}