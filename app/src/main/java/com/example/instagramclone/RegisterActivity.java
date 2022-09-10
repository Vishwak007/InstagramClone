package com.example.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText username,email, name, password;
    private TextView loginuser;
    private Button btnRegister;

    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    ProgressDialog pD;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        name = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        loginuser = findViewById(R.id.loginuser);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        pD = new ProgressDialog(RegisterActivity.this);

        loginuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mUsername = username.getText().toString();
                String mName = name.getText().toString();
                String mMail = email.getText().toString();
                String mPassword = password.getText().toString();

                if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mName) || TextUtils.isEmpty(mMail)
                || TextUtils.isEmpty(mPassword)){
                    Toast.makeText(RegisterActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();

                }
                else
                    createUser(mMail, mName, mPassword, mUsername);
            }
        });




    }

    private void createUser(final String Mail, final String Name, String Password, final String Username) {
        pD.setMessage("Please Wait...");
        pD.show();

        mAuth.createUserWithEmailAndPassword(Mail,Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> map = new HashMap();
                map.put("username", Username);
                map.put("name", Name);
                map.put("email", Mail);
                map.put("userid", mAuth.getCurrentUser().getUid());
                map.put("bio", "");
                map.put("imageurl","default");

                mRootRef.child("User").child(mAuth.getCurrentUser().getUid()).setValue(map).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            pD.dismiss();
                            Toast.makeText(RegisterActivity.this, "Goto Settings "+
                                    "Update the profile for better Experience", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pD.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}