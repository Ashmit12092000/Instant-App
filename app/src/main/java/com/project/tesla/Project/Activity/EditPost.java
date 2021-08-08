package com.project.tesla.Project.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.tesla.Project.Model.Post;
import com.project.tesla.R;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

public class EditPost extends AppCompatActivity {

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
    String updatedCaption;
    Toolbar toolbar;
    String postID,postImg,postUid,postCaption;
    ImageView back_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        FirebaseApp.initializeApp(this);
        db = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        postID=getIntent().getStringExtra("postID");
        postImg=getIntent().getStringExtra("postImg");
        postCaption=getIntent().getStringExtra("captionText");
        postUid=getIntent().getStringExtra("uid");


        mauth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(EditPost.this);
        back_btn=findViewById(R.id.edit_back_btn);
        dialog.setMessage("Detecting Changes....");
        toolbar=findViewById(R.id.edit_toolbar9);
        Caption_text = findViewById(R.id.edit_caption_Text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Upload_img = findViewById(R.id.edit_upload_image);
        Glide.with(EditPost.this).load(postImg).placeholder(R.drawable.photo).into(Upload_img);
        Caption_text.setText(postCaption);


        add_post_btn=findViewById(R.id.edit_Add_post_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditPost.this,MainActivity.class));
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
            selected_video = data.getData();
        }
    }
    private void AddPost (){

        AlertDialog.Builder alertdialog=new AlertDialog.Builder(EditPost.this);
        alertdialog.setTitle("Edit Confirmation");
        alertdialog.setMessage("Are you sure want to edit the changes ?");
        alertdialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface sdialog, int which) {
                dialog.show();
                if(!Caption_text.getText().toString().equals(postCaption)){
                    String caption=Caption_text.getText().toString();
                    FirebaseDatabase.getInstance().getReference()
                            .child("Posts")
                            .child(postID)
                            .child("caption_txt")
                            .setValue(caption)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Toast.makeText(EditPost.this, "Caption Updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

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
                                        String image_uri = uri.toString();

                                        db.getReference()
                                                .child("Posts")
                                                .child(postID)
                                                .child("image_uri")
                                                .setValue(image_uri);
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
        alertdialog.setNegativeButton("No",null);
        alertdialog.setCancelable(true);
        alertdialog.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditPost.this,MainActivity.class));
        finish();
    }
}