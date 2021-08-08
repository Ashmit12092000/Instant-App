package com.project.tesla.Project.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserInfoEdit extends AppCompatActivity {
    TextInputEditText names,email,phone,staus_text;
    ImageView myprofileimage,image_btn;
    Button update;
    FirebaseAuth mauth;
    FirebaseDatabase db;
    FirebaseStorage storage;
    ProgressDialog dialog;
    String status_retrieved="";
    Uri selected_image;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_edit);
        FirebaseApp.initializeApp(this);
        mauth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        toolbar=findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        myprofileimage= findViewById(R.id.myprofileimage);
        image_btn= findViewById(R.id.imagebutton);
        names= findViewById(R.id.profile_name);
        email= findViewById(R.id.profile_Email);
        db=FirebaseDatabase.getInstance();
        DatabaseReference docref= db.getReference("users");
        Query query=docref.orderByChild("uid").equalTo(mauth.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String name=""+ds.child("name").getValue(String.class);
                    String Email=""+ds.child("email").getValue(String.class);
                    String pic=ds.child("profile_image").getValue(String.class);
                    names.setText(name);
                    email.setText(Email);
                    try {

                        Picasso.get()
                                .load(pic)
                                .placeholder(R.drawable.user)
                                .into(myprofileimage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        update= findViewById(R.id.profile_button);
        dialog=new ProgressDialog(this);
        dialog.setMessage("Updating please wait...");
        dialog.setCancelable(false);

        myprofileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,10);
            }
        });
        image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,11);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String name = Objects.requireNonNull(names.getText()).toString();
                String Email = Objects.requireNonNull(email.getText()).toString();
                User user = new User();
                user.setName(name);
                user.setEmail(Email);

                if(!(Email.equals(Objects.requireNonNull(mauth.getCurrentUser()).getEmail()))) {
                    db.getReference().child("users").child(Objects.requireNonNull(mauth.getUid())).child("email").setValue(user.getEmail())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Toast.makeText(UserInfoEdit.this, "Email have ben updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                if(!(name.equals(Objects.requireNonNull(mauth.getCurrentUser()).getDisplayName()))) {
                    db.getReference().child("users").child(Objects.requireNonNull(mauth.getUid())).child("name").setValue(user.getName())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    dialog.dismiss();
                                    Toast.makeText(UserInfoEdit.this, "Name have ben updated", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                db.getReference()
                        .child("users")
                        .child(Objects.requireNonNull(mauth.getUid()))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot sp:snapshot.getChildren()){
                                    String st= sp.child("status").getValue(String.class);
                                    status_retrieved=st;
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }


        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10 && resultCode==RESULT_OK && data.getData()!=null) {
            myprofileimage.setImageURI(data.getData());
            selected_image = data.getData();
            if(selected_image!=null){
                dialog.setMessage("Updating Profile photo.....");
                dialog.show();
                StorageReference reference = FirebaseStorage.getInstance().getReference().child("users").child(Objects.requireNonNull(mauth.getUid()));
                reference.putFile(selected_image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String image_uri = uri.toString();
                                    User user= new User();
                                    user.setImage_uri(image_uri);
                                    db.getReference().child("users").child(Objects.requireNonNull(mauth.getCurrentUser())
                                            .getUid()).child("profile_image")
                                            .setValue(user.getImage_uri()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            Toast.makeText(UserInfoEdit.this,"Profile photo have been updated",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        }
        else if(requestCode==11 && resultCode==RESULT_OK && data.getData()!=null) {
            if (selected_image != null){
                StorageReference reference = FirebaseStorage.getInstance().getReference().child("users").child(Objects.requireNonNull(mauth.getUid()));
                reference.putFile(selected_image).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String image_uri = uri.toString();
                                    User user = new User();
                                    user.setImage_uri(image_uri);
                                    db.getReference().child("users").child(Objects.requireNonNull(mauth.getCurrentUser())
                                            .getUid()).child("profile_image")
                                            .setValue(user.getImage_uri()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            dialog.dismiss();
                                            Toast.makeText(UserInfoEdit.this, "Profile photo have been updated", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(UserInfoEdit.this, "Please wait  few second for reflecting !", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        }
    }
    }
