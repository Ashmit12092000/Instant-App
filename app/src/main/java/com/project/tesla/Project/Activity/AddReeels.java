package com.project.tesla.Project.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;
import com.project.tesla.Project.Fragments.ReelsFragment;
import com.project.tesla.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.tesla.Project.Model.Post;
import com.project.tesla.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AddReeels extends AppCompatActivity {
    FirebaseDatabase db;
    FirebaseAuth mauth;
    ProgressDialog dialog;
    EditText Caption_text;
    ImageView Upload_img;
    Button add_post_btn;
    FirebaseStorage storage;
    Uri selected_video;
    String posted_username;
    private Bitmap bitmap;
    Toolbar toolbar;
    ImageView back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_reeels);
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        mauth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(AddReeels.this);
        back_btn=findViewById(R.id.reels_back_btn);
        dialog.setMessage("Uploading....");
        toolbar=findViewById(R.id.reels_toolbar9);
        Caption_text = findViewById(R.id.reels_caption_Text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Upload_img = findViewById(R.id.reels_upload_image);
        add_post_btn=findViewById(R.id.reels_Add_post_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddReeels.this,MainActivity.class));
                finish();
            }
        });
        Upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, 100);
            }
        });

        add_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPost();
            }
        });
        FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(mauth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            posted_username=snapshot.child("name").getValue(String.class);
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
        if (requestCode == 100 && resultCode == RESULT_OK && data.getData() != null) {
            Upload_img.setImageURI(data.getData());
            selected_video = data.getData();
        }
    }
    private void AddPost (){
        dialog.show();

        if(Caption_text.getText().toString().isEmpty()) {
            Caption_text.setError("Please enter caption!");
            Caption_text.requestFocus();
        }
        else if(selected_video!=null){


            Calendar calendar=Calendar.getInstance();
            StorageReference reference= FirebaseStorage.getInstance().getReference().child("Posts").child(calendar.getTime()+"");
            reference.putFile(selected_video).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dialog.dismiss();
                                String cap_txt = Caption_text.getText().toString();
                                String video_uri = uri.toString();
                                String uid =mauth.getUid();
                                Date date=new Date();
                                String key=db.getReference().push().getKey();
                                Post post=new Post(cap_txt,null,date.getTime());
                                post.setVideo_url(video_uri);
                                post.setPost_type("video");
                                post.setUid(uid);
                                post.setUsername(posted_username);
                                db.getReference()
                                        .child("Reels")
                                        .child(key)
                                        .setValue(post)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void a) {
                                                startActivity(new Intent(AddReeels.this, MainActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddReeels.this, "Something went wrong.. Try Again", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                db.getReference()
                                        .child("PostCount")
                                        .child(uid)
                                        .child(key)
                                        .setValue(post)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void a) {
                                                startActivity(new Intent(AddReeels.this,MainActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddReeels.this, "Something went wrong.. Try Again", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        });
                    }
                }
            });
        }
        else{
            String uid =mauth.getUid();
            Date date=new Date();
            String cap_txt = Caption_text.getText().toString();

            Post post=new Post(cap_txt,"No image",date.getTime());
            post.setUid(uid);
            db.getReference().child("Posts")
                    .push()
                    .setValue(post)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void a) {
                            startActivity(new Intent(AddReeels.this,MainActivity.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddReeels.this, "Something went wrong.. Try Again", Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddReeels.this,MainActivity.class));
        finish();
    }
}