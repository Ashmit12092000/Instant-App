package com.project.tesla.Project.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.project.tesla.R;

import java.util.HashMap;
import java.util.Objects;

public class Registration extends AppCompatActivity {
    TextInputEditText Name,Email,Password;
    FirebaseAuth mAuth;
    ProgressDialog dialog;
    Button reg_btn;
    FirebaseDatabase  db;
    TextView login_text_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(Registration.this);
        dialog.setMessage("Loading...");
        Name=findViewById(R.id.reg_name);
        Email=findViewById(R.id.reg_email);
        Password=findViewById(R.id.reg_password);
        login_text_btn=findViewById(R.id.login_text_btn);
        db=FirebaseDatabase.getInstance();
        login_text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this,LoginActivity.class));
            }
        });
        reg_btn=findViewById(R.id.reg_btn);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= Objects.requireNonNull(Email.getText()).toString();
                String password= Objects.requireNonNull(Password.getText()).toString();
                String name= Objects.requireNonNull(Name.getText()).toString();
                if(email.isEmpty()){
                    Email.setError("Please Enter Email");
                    Email.requestFocus();
                }
                else if(name.isEmpty()){
                    Name.setError("Please Enter Name");
                    Name.requestFocus();
                }
                else if(password.isEmpty()){
                    Password.setError("Please Enter Password");
                    Password.requestFocus();
                }

                else if(name.isEmpty() && email.isEmpty() && password.isEmpty()){
                    Email.setError("Please Enter Email");
                    Email.requestFocus();
                    Name.setError("Please Enter Name");
                    Name.requestFocus();
                    Password.setError("Please Enter Password");
                    Password.requestFocus();
                }
                else {
                    if(password.length()>=6) {
                        RegisterUser(email, password, name);
                    }
                    else{
                        Password.setError("Password cannot be < 6 characters");
                        Password.requestFocus();
                        Toast.makeText(Registration.this, "Password cannot be < 6 characters", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

    }

    private void RegisterUser(String email, String password, String name) {
        dialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            HashMap<String,Object> userdetails=new HashMap<>();
                            userdetails.put("name",name);
                            userdetails.put("email",email);
                            userdetails.put("password",password);

                            db.getReference()
                                    .child("users")
                                    .child(user.getUid())
                                    .setValue(userdetails)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Registration.this, "Registered Successfully...", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                            startActivity(new Intent(Registration.this,LoginActivity.class));
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Registration.this, "Registration failed.",
                                   Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }
                });
    }


}