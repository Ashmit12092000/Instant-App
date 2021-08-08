package com.project.tesla.Project.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.tesla.Project.Adapters.UserAdapter;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    FirebaseAuth mauth;
    FirebaseDatabase db;
    DatabaseReference userRef;
    ShimmerRecyclerView recyclerView;
    UserAdapter adapter;
    ArrayList<User> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mauth=FirebaseAuth.getInstance();
        db=FirebaseDatabase.getInstance();
        userRef=db.getReference("users");
        recyclerView=findViewById(R.id.shimmerUserRecyclerview);
        users=new ArrayList<>();
        adapter=new UserAdapter(MessageActivity.this,users);
        recyclerView.showShimmerAdapter();
        recyclerView.setAdapter(adapter);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user=dataSnapshot.getValue(User.class);
                    try {
                        if(!mauth.getUid().equals(user.getUid())) {
                            users.add(user);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                recyclerView.hideShimmerAdapter();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}