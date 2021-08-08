package com.project.tesla.Project.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
import com.google.firebase.database.FirebaseDatabase;
import com.project.tesla.Project.Model.User;
import com.project.tesla.R;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    TextInputEditText Email,Password;
    FirebaseAuth mAuth;
    Button login_btn;
    ProgressDialog dialog;
    FirebaseDatabase db;
    Button google_login_btn;
    TextView reg_btn_text;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        Email=findViewById(R.id.Email);
        dialog=new ProgressDialog(LoginActivity.this);
        dialog.setMessage("Logging in....");
        db=FirebaseDatabase.getInstance();
        Password=findViewById(R.id.login_password);
        login_btn=findViewById(R.id.login_btn);
        google_login_btn=findViewById(R.id.google_btn);
        reg_btn_text=findViewById(R.id.reg_text_btn);
        mAuth=FirebaseAuth.getInstance();

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=Email.getText().toString();
                String password=Password.getText().toString();
                if(email.isEmpty()){
                    Email.setError("Please Enter Email");
                    Email.requestFocus();
                }
                else if(password.isEmpty()){
                    Password.setError("Please Enter Password");
                    Password.requestFocus();
                }
                else {
                    if(password.length()>=6){
                        login(email, password);
                    }
                    else{
                        Password.setError("Password cannot be < 6 characters");
                        Password.requestFocus();
                        Toast.makeText(LoginActivity.this, "Password cannot be < 6 characters", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        reg_btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,Registration.class));
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        google_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

    }

    private void login(String email, String password) {
        dialog.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            finish();
                            Toast.makeText(LoginActivity.this, "Login Successfull",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));

                            FirebaseUser user = mAuth.getCurrentUser();

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                           // Log.w(TAG, "signInWithEmail:failure", task.getException());
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                    }


                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        dialog.setMessage("Logging in....");
        dialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                           // Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser fuser = mAuth.getCurrentUser();
                            HashMap<String,Object> userdetails=new HashMap<>();
                            userdetails.put("name",fuser.getDisplayName());
                            userdetails.put("email",fuser.getEmail());
                            userdetails.put("uid",fuser.getUid());
                            db.getReference()
                                    .child("users")
                                    .child(fuser.getUid())
                                    .setValue(userdetails)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(LoginActivity.this, "Registered Successfully...", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                            Intent i=new Intent(LoginActivity.this,MainActivity.class);
                            i.putExtra("name",fuser.getDisplayName());
                            startActivity(i);
                            finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                           // Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // updateUI(null);
                            dialog.dismiss();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }
    }
}
