package com.project.tesla.Project.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager2.widget.ViewPager2;
import com.allattentionhere.autoplayvideos.AAH_CustomRecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tesla.Project.Activity.AddReeels;
import com.project.tesla.Project.Activity.AddpostActivity;
import com.project.tesla.Project.Activity.MainActivity;
import com.project.tesla.Project.Adapters.ReelsAdapter;
import com.project.tesla.Project.Model.Post;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;

import java.util.ArrayList;
import java.util.List;

public class ReelsFragment extends Fragment {


    public ReelsFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_reels, container, false);

        List<Post> videoList=new ArrayList<>();
        ImageView add_reels_btn=view.findViewById(R.id.add_reels_btn);
        ViewPager2 viewPager=view.findViewById(R.id.viewpager2);
        ReelsAdapter adapter=new ReelsAdapter(getContext(),videoList);
        MainActivity activity;
        ImageView back_btn=view.findViewById(R.id.reel_back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),MainActivity.class));

            }
        });
        activity=(MainActivity) getActivity();
        activity.getSupportActionBar().hide();

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
                    startActivity(new Intent(getContext(), AddReeels.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return  view;
    }



}