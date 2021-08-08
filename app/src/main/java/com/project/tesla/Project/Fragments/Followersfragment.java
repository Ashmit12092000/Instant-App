package com.project.tesla.Project.Fragments;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.project.tesla.Project.Activity.FollowingInfo;
import com.project.tesla.Project.Activity.UserProfileActivity;
import com.project.tesla.Project.Adapters.FollowersListAdapter;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;

import java.util.ArrayList;

public class Followersfragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_followersfragment, container, false);
        FollowingInfo activity=(FollowingInfo) getActivity();
        Bundle bundle=activity.getData();
        String uid=bundle.getString("uid");

        DatabaseReference followersRef=FirebaseDatabase.getInstance().getReference().child("followers").child(uid);
        FirebaseAuth mauth=FirebaseAuth.getInstance();

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        ArrayList<User> users=new ArrayList<>();
        FollowersListAdapter followersListAdapter=new FollowersListAdapter(getContext(),users);
        RecyclerView recyclerView=view.findViewById(R.id.followers_recyclerView);

        followersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    try {
                        User user = dataSnapshot.getValue(User.class);
                        users.add(user);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Something wrong at our side...", Toast.LENGTH_SHORT).show();
                    }
                }
                recyclerView.setAdapter(followersListAdapter);
                followersListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return  view;
    }

}