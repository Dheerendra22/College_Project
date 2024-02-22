package com.College.Vindhya_Group_Of_Institutions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText email, password;
    FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;
    Button login;
    SharedPreferences sharedPreferences;
    private Progress_Dialog progressDialog;

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
        fireStore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        progressDialog = new Progress_Dialog(Login.this);

        if (fAuth.getCurrentUser() != null) {
            redirectToDashboard();
        }

        login.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        progressDialog.setMessage("Logging...");
        progressDialog.show();

        String mEmail = email.getText().toString().trim();
        String mPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword) || mPassword.length() < 6) {
            dismissProgressDialog();
            Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authenticate the user!
        fAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserRole(mEmail);
                    } else {
                        dismissProgressDialog();
                        Toast.makeText(Login.this, "Something Error!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(String userEmail) {
        DocumentReference userRef = fireStore.collection("Students").document(userEmail);
        userRef.get()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String role = document.getString("Role");
                            handleUserRole(role);
                        } else {
                            dismissProgressDialog();
                            Toast.makeText(Login.this, "Please Try Again Later! ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dismissProgressDialog();
                        Toast.makeText(Login.this, "Please Try Again Later! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    dismissProgressDialog();
                    Toast.makeText(Login.this, "Please Try Again Later! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void handleUserRole(String role) {
        if ("Student".equals(role) || "Admin".equals(role) || "Teacher".equals(role)) {
            saveToSharedPreferences("Role", role);
            dismissProgressDialog();
            Toast.makeText(Login.this, "Logged in Successfully. ", Toast.LENGTH_SHORT).show();
            redirectToDashboard();
        } else {
            dismissProgressDialog();
            Toast.makeText(Login.this, "Invalid Role", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToDashboard() {
        String role = sharedPreferences.getString("Role", "");
        Class<?> destinationClass;

        if ("Student".equals(role)) {
            destinationClass = Student_Dash.class;
        } else if ("Admin".equals(role) || "Teacher".equals(role)) {
            destinationClass = Admin_Dashboard.class;
        } else {
            return; // Handle any other roles or situations as needed
        }

        startActivity(new Intent(getApplicationContext(), destinationClass));
        finish();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void saveToSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
