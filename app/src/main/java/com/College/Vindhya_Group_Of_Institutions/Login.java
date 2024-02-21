package com.College.Vindhya_Group_Of_Institutions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText email, password;
    FirebaseAuth fAuth;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_activity);


        login = findViewById(R.id.btnLogin);
        email = findViewById(R.id.edtUser);
        password = findViewById(R.id.edtPassword);
        fAuth = FirebaseAuth.getInstance();


        if (fAuth.getCurrentUser() != null) {
            // User is already logged in, navigate to Admin_Dashboard
            startActivity(new Intent(getApplicationContext(), Admin_Dashboard.class));
            finish(); // Optional: Close the current activity to prevent the user from coming back using the back button.
        }


        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("Login...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(mEmail)) {
                    email.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(mPassword)) {
                    password.setError("Please enter Password");
                    return;
                }
                if (mPassword.length() < 6) {
                    password.setError("Password must be 6 letters or more! ");
                    return;
                }

                // Authenticate the user!
                fAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Logged in Successfully. ", Toast.LENGTH_SHORT).show();


                        startActivity(new Intent(getApplicationContext(), Admin_Dashboard.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Something Error!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });
    }


}