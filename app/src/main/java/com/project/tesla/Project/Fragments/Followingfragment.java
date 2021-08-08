package com.project.tesla.Project.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.project.tesla.Project.Activity.FollowingInfo;

import com.project.tesla.Project.Adapters.FollowingListAdapter;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;

import java.util.ArrayList;

public class Followingfragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_followingfragment, container, false);
        FirebaseAuth mauth=FirebaseAuth.getInstance();
        DatabaseReference followingRef=FirebaseDatabase.getInstance().getReference().child("following").child(mauth.getUid());
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        ArrayList<User> users=new ArrayList<>();
        FollowingListAdapter followingListAdapter=new FollowingListAdapter(getContext(),users);
        RecyclerView recyclerView=view.findViewById(R.id.following_recyclerView);

        followingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    users.add(user);
                }
                recyclerView.setAdapter(followingListAdapter);
                followingListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}