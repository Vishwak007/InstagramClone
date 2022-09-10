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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {
    private EditText mail,password;
    private TextView registerUser;
    private Button btnLogIn;
    private FirebaseAuth nAuth;
    private ProgressDialog pD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mail = findViewById(R.id.email1);
        password = findViewById(R.id.password1);
        btnLogIn = findViewById(R.id.btnLogIn);
        registerUser = findViewById(R.id.registeruser);

        nAuth = FirebaseAuth.getInstance();
        pD = new ProgressDialog(LogInActivity.this);

        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nEmail = mail.getText().toString();
                String nPassword = password.getText().toString();

                if (TextUtils.isEmpty(nEmail) || TextUtils.isEmpty(nPassword)){
                    Toast.makeText(LogInActivity.this, "Incomplete Credentials", Toast.LENGTH_SHORT).show();

                }
                else{
                    signinUser(nEmail, nPassword);
                }


            }
        });
        
    }

    private void signinUser(String Email, String Password) {
        pD.setMessage("Processing...");
        pD.show();

        nAuth.signInWithEmailAndPassword(Email, Password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                pD.dismiss();

                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

                Toast.makeText(LogInActivity.this, "You're Logged In", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pD.dismiss();
                Toast.makeText(LogInActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}