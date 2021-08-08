package com.project.tesla.Project.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.project.tesla.Project.Activity.FollowingInfo;
import com.project.tesla.Project.Activity.UserInfoEdit;
import com.project.tesla.Project.Activity.UserProfileActivity;
import com.project.tesla.Project.Adapters.UserGridPostsAdapter;
import com.project.tesla.Project.Model.Post;
import com.project.tesla.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_profile, container, false);
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        TextView followers_count = view.findViewById(R.id.followers_count);
        TextView following_count = view.findViewById(R.id.following_count);
        ImageView imageView = view.findViewById(R.id.upload_profile_img);
        TextView post_count = view.findViewById(R.id.postcount);
        Button edit_btn = view.findViewById(R.id.edit_profile_btn);
        HashMap<String,Object> follower_hashMap = new HashMap<>();
        HashMap<String,Object> hashMap = new HashMap<>();
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        db.getReference().child("users").child(mauth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String image_uri = snapshot.child("profile_image").getValue(String.class);
                follower_hashMap.put("name", name);
                follower_hashMap.put("profile_image", image_uri);
                follower_hashMap.put("isfollower", true);
                hashMap.put("name",name);
                hashMap.put("profile_image",image_uri);
                hashMap.put("isfollowing",true);


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        RecyclerView recyclerView=view.findViewById(R.id.post_recycler_view);
        TextView Posts=view.findViewById(R.id.Posts);
        TextView nopost=view.findViewById(R.id.nopost);
        TextView Followers=view.findViewById(R.id.Followers);
        TextView Following=view.findViewById(R.id.Following);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        ArrayList<Post> postList=new ArrayList<>();
        UserGridPostsAdapter adapter=new UserGridPostsAdapter(getContext(),postList);
        recyclerView.setAdapter(adapter);
        db= FirebaseDatabase.getInstance();
        db.getReference("users").child(mauth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String image_uri=snapshot.child("profile_image").getValue(String.class);
                    Glide.with(getContext()).load(image_uri).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Button follow_btn=view.findViewById(R.id.follow_btn);
        ProgressDialog dialog=new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UserInfoEdit.class));
            }
        });

        db.getReference().child("users").child(mauth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String name=snapshot.child("name").getValue(String.class);
                String image_uri=snapshot.child("profile_image").getValue(String.class);
                Glide.with(getContext()).load(image_uri).placeholder(R.drawable.user).into(imageView);
                hashMap.put("name",name);
                hashMap.put("profile_image",image_uri);
                hashMap.put("isfollowing",true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (follow_btn.getTag().equals("not following")) {

                    FirebaseDatabase.getInstance().getReference("followers").child(mauth.getUid()).child(mauth.getUid()).setValue(hashMap);

                    FirebaseDatabase.getInstance().getReference("following").child(mauth.getUid()).child(mauth.getUid()).setValue(hashMap);
                }
                else{
                    FirebaseDatabase.getInstance().getReference("followers").child(mauth.getUid()).child(mauth.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference("following").child(mauth.getUid()).child(mauth.getUid()).removeValue();
                }
            }
        });
        Followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString("uid",mauth.getUid());

                Followersfragment followersfragment=new Followersfragment();
                followersfragment.setArguments(bundle);
                Intent intent= new Intent(getContext(), FollowingInfo.class);
                intent.putExtra("uid",mauth.getUid());
                startActivity(intent);
            }
        });
        Following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(),FollowingInfo.class);
                intent.putExtra("uid",mauth.getUid());
                startActivity(intent);
            }
        });
        FirebaseDatabase.getInstance().getReference()
                .child("PostCount")
                .child(mauth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            nopost.setVisibility(View.GONE);
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Post post = snapshot1.getValue(Post.class);
                                post.setPost_id(snapshot1.getKey());
                                postList.add(post);
                            }
                            adapter.notifyDataSetChanged();
                            recyclerView.scheduleLayoutAnimation();
                        }
                        else{
                            nopost.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        getFollowerstatus(post_count,mauth,follow_btn);
        countfollowers(followers_count,mauth);
        countfollowing(following_count,mauth);
        countPost(post_count,mauth);
        return  view;
    }

    private void getFollowerstatus(TextView post_count, FirebaseAuth mauth, Button follow_btn) {
        DatabaseReference followersRef =FirebaseDatabase.getInstance().getReference("followers").child(mauth.getUid());
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

    private void countPost(TextView post_count, FirebaseAuth mauth) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("PostCount").child(mauth.getUid());
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


    private void countfollowing(TextView following_count, FirebaseAuth mauth) {
        DatabaseReference following=FirebaseDatabase.getInstance().getReference("following").child(mauth.getUid());
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

    private void countfollowers(TextView followers_count, FirebaseAuth mauth) {
        DatabaseReference followersRef =FirebaseDatabase.getInstance().getReference("followers").child(mauth.getUid());
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


}