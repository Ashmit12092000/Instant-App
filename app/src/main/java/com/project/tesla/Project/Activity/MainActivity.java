package com.project.tesla.Project.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import com.google.firebase.FirebaseApp;
import android.os.AsyncTask;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.project.tesla.Project.Adapters.PostAdapter;
import com.project.tesla.Project.Adapters.StatusAdapter;
import com.project.tesla.Project.CustomClasses._SwipeActivityClass;
import com.project.tesla.Project.Fragments.HomeFragment;
import com.project.tesla.Project.Fragments.MyProfileFragment;
import com.project.tesla.Project.Fragments.ReelsFragment;
import com.project.tesla.Project.Model.Post;
import com.project.tesla.Project.Model.Status;
import com.project.tesla.Project.Model.User;
import com.project.tesla.Project.Model.UserStatus;
import com.project.tesla.R;
import com.thecode.aestheticdialogs.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ChipNavigationBar chipNavigationBar;
    ArrayList<UserStatus> statuses;
    ProgressDialog dialog;
    StatusAdapter statusAdapter;
    ImageView statusadd_btn;
    LinearLayout linearLayout;
    FirebaseAuth mauth;
    FirebaseDatabase db;
    String name;
    AestheticDialog negative;
    ShimmerRecyclerView recyclerView;
    String image_uri;
    Toolbar toolbar;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        db=FirebaseDatabase.getInstance();
        dialog=new ProgressDialog(this);
        mauth=FirebaseAuth.getInstance();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        linearLayout=findViewById(R.id.linearLayout);
        dialog.setMessage("Uploading status....");
        recyclerView=findViewById(R.id.shimmer_story_recycler_view);
        statuses=new ArrayList<>();
        statusAdapter=new StatusAdapter(MainActivity.this,statuses);
        recyclerView.setAdapter(statusAdapter);
        boolean connection=isConnected();
        if(!connection) {

                new AestheticDialog.Builder(this, DialogStyle.CONNECTIFY, DialogType.ERROR)
                        .setTitle("Connection Failed")
                        .setMessage("Please check your internet connection!")
                        .setCancelable(true)
                        .setAnimation(DialogAnimation.SLIDE_UP)
                        .setOnClickListener(new OnDialogClickListener() {
                            @Override
                            public void onClick(@NotNull AestheticDialog.Builder builder) {

                            }
                        })
                        .show();
                //actions...

            //actions...

        }



        statusadd_btn=findViewById(R.id.addstatus_btn);

        db.getReference().child("users").child(mauth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                     name=snapshot.child("name").getValue(String.class);
                     image_uri=snapshot.child("profile_image").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        db.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    statuses.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                       UserStatus userStatus=new UserStatus();
                       userStatus.setName(dataSnapshot.child("name").getValue(String.class));
                       userStatus.setProfileImage(dataSnapshot.child("profileImage").getValue(String.class));
                       userStatus.setLastupdated(dataSnapshot.child("lastupdated").getValue(Long.class));
                       ArrayList<Status> status=new ArrayList<>();
                       for(DataSnapshot status_snapshot:dataSnapshot.child("statuses").getChildren()){
                           Status st=status_snapshot.getValue(Status.class);
                           //st.setStatus_id(status_snapshot.getKey());
                           status.add(st);
                       }
                       userStatus.setStatuses(status);
                       statuses.add(userStatus);
                    }
                    recyclerView.hideShimmerAdapter();
                    statusAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        chipNavigationBar = findViewById(R.id.bottom_nav_bar);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_home,
                true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container,
                        new HomeFragment()).commit();
        bottomMenu();
        statusadd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,100);
            }
        });

    }

    /*@Override
    protected void onSwipeRight() {
        Toast.makeText(this, "Right swiped", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSwipeLeft() {
        Toast.makeText(this, "Left swiped", Toast.LENGTH_SHORT).show();
    }*/


    private Boolean isConnected() {
        boolean connected=false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && resultCode==RESULT_OK && data.getData()!=null){
            dialog.show();
            FirebaseStorage storage=FirebaseStorage.getInstance();
            Date date=new Date();
            StorageReference reference=storage.getReference().child("status").child(date.getTime()+"");
            reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dialog.dismiss();
                                UserStatus userStatus=new UserStatus();
                                userStatus.setName(name);
                                userStatus.setProfileImage(image_uri);
                                userStatus.setLastupdated(date.getTime());

                                HashMap<String,Object>hashMap=new HashMap<>();
                                hashMap.put("name",userStatus.getName());
                                hashMap.put("profileImage",userStatus.getProfileImage());
                                hashMap.put("lastupdated",userStatus.getLastupdated());
                                String imageUrl=uri.toString();
                                Status status=new Status(imageUrl,userStatus.getLastupdated());
                                status.setSUid(mauth.getUid());
                                db.getReference().child("stories")
                                        .child(mauth.getUid())
                                        .updateChildren(hashMap);
                                db.getReference().child("stories")
                                        .child(mauth.getUid())
                                        .child("statuses")
                                        .push()
                                        .setValue(status);
                            }
                        });
                    }
                }
            });
        }
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener
                (new ChipNavigationBar.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int i) {
                        Fragment fragment = null;
                        switch (i){
                            case R.id.bottom_nav_home:
                                linearLayout.setVisibility(View.VISIBLE);
                                fragment = new HomeFragment();
                                break;
                            case R.id.reels_btn:
                                startActivity(new Intent(MainActivity.this,ReelsActivity.class));
                                break;

                            case R.id.bottom_nav_addpost:
                                try {
                                    linearLayout.setVisibility(View.GONE);
                                    startActivity(new Intent(getApplicationContext(), AddpostActivity.class));
                                    break;
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            case R.id.bottom_nav_user:
                                linearLayout.setVisibility(View.GONE);
                                fragment = new MyProfileFragment();

                                break;
                        }
                        try {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, fragment).commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment fragments = null;
        switch (item.getItemId()){

            case R.id.logout:
                mauth.signOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
                break;
            case R.id.messenger:
                startActivity(new Intent(MainActivity.this,MessageActivity.class));
                //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}