package com.project.tesla.Project.Activity;

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
import com.google.firebase.FirebaseApp;
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

public class AddpostActivity extends AppCompatActivity {
    FirebaseDatabase db;
    FirebaseAuth mauth;
    ProgressDialog dialog;
    EditText Caption_text;
    ImageView Upload_img;
    Button add_post_btn;
    FirebaseStorage storage;
    Uri selected_image;
    String posted_username;
    private Bitmap bitmap;
    Toolbar toolbar;
    ImageView back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        mauth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(AddpostActivity.this);
        back_btn=findViewById(R.id.back_btn);
        dialog.setMessage("Uploading....");
        toolbar=findViewById(R.id.toolbar2);
        Caption_text = findViewById(R.id.caption_Text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Upload_img = findViewById(R.id.upload_image);
        add_post_btn=findViewById(R.id.Add_post_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddpostActivity.this,MainActivity.class));
                finish();
            }
        });
        Upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
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
            selected_image = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selected_image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void AddPost (){
        dialog.show();

        if(Caption_text.getText().toString().isEmpty()) {
            Caption_text.setError("Please enter caption!");
            Caption_text.requestFocus();
        }
        else if(selected_image!=null){

            Post post=new Post();





            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            int currentBitmapWidth = bitmap.getWidth();
            int currentBitmapHeight = bitmap.getHeight();

            int ivWidth = Upload_img.getWidth();
            int ivHeight = Upload_img.getHeight();
            int newWidth = ivWidth;

            int newHeight = (int) Math.floor((double) currentBitmapHeight *( (double) newWidth / (double) currentBitmapWidth));

            bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            Calendar calendar=Calendar.getInstance();
            StorageReference reference= FirebaseStorage.getInstance().getReference().child("Posts").child(calendar.getTime()+"");
            byte[] b = stream.toByteArray();
            post.setBitmap(bitmap);
            reference.putBytes(b).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dialog.dismiss();
                                String cap_txt = Caption_text.getText().toString();
                                String image_uri = uri.toString();
                                String uid =mauth.getUid();
                                Date date=new Date();
                                String key=db.getReference().push().getKey();
                                Post post=new Post(cap_txt,image_uri,date.getTime());

                                post.setUid(uid);

                                post.setUsername(posted_username);
                                post.setPost_type("photo");
                                db.getReference()
                                        .child("Posts")
                                        .child(key)
                                        .setValue(post)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void a) {
                                                startActivity(new Intent(AddpostActivity.this,MainActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddpostActivity.this, "Something went wrong.. Try Again", Toast.LENGTH_SHORT).show();
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
                                                startActivity(new Intent(AddpostActivity.this,MainActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddpostActivity.this, "Something went wrong.. Try Again", Toast.LENGTH_SHORT).show();
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
                            startActivity(new Intent(AddpostActivity.this,MainActivity.class));
                            finish();
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddpostActivity.this, "Something went wrong.. Try Again", Toast.LENGTH_SHORT).show();
                }
            });


        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AddpostActivity.this,MainActivity.class));
        finish();
    }
}