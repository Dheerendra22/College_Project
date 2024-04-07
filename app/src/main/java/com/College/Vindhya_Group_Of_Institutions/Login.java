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

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    String mEmail , mPassword ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set full-screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_activity);

        // Initialize UI elements and Firebase instances
        login = findViewById(R.id.btnLogin);
        email = findViewById(R.id.edtUser);
        password = findViewById(R.id.edtPassword);
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        progressDialog = new Progress_Dialog(Login.this);

        // Redirect to the dashboard if the user is already logged in
        if (fAuth.getCurrentUser() != null) {
            redirectToDashboard();
        }

        // Set up click listener for login button
        login.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        progressDialog.setMessage("Logging...");
        progressDialog.show();

        // Retrieve email and password from the UI
        mEmail = email.getText().toString().trim();
        mPassword = password.getText().toString().trim();

        // Validate email and password
        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword) || mPassword.length() < 6) {
            dismissProgressDialog();
            Toast.makeText(this, "Please enter valid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authenticate the user
        fAuth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserRole(mEmail);
                    } else {
                        dismissProgressDialog();
                        Toast.makeText(Login.this, "Something Error!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(Login.this, "Please try again later!", Toast.LENGTH_SHORT).show();
                });
    }

    private void checkUserRole(String userEmail) {

        DocumentReference userRef = fireStore.collection("Faculty").document(userEmail);
        userRef.get()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // User is a faculty
                            String role = document.getString("Role");
                            saveToSharedPreferences("Email", document.getString("Email"));
                            saveToSharedPreferences("Password", document.getString("Password"));
                            handleUserRole(role);
                        } else {
                            // Check if the user is a student
                            checkInStudent(userEmail);
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    dismissProgressDialog();
                    Toast.makeText(Login.this, "Please Try Again Later! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkInStudent(String email) {
        DocumentReference userRef1 = fireStore.collection("Students").document(email);
        userRef1.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document != null && document.exists()) {
                // User is a student
                String role = document.getString("Role");
                saveToSharedPreferences("Email", document.getString("Email"));
                saveToSharedPreferences("Password", document.getString("Password"));
                handleUserRole(role);
            } else {
                // Invalid user
                dismissProgressDialog();
                deleteCurrentUser(mEmail,mPassword);
                Toast.makeText(Login.this, "You are not a valid User.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            dismissProgressDialog();
            Toast.makeText(Login.this, "Please Try Again Later! ", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleUserRole(String role) {
        if ("Student".equals(role) || "Admin".equals(role) || "Teacher".equals(role)) {
            // Valid user role
            saveToSharedPreferences("Role", role);
            dismissProgressDialog();
            Toast.makeText(Login.this, "Logged in Successfully. ", Toast.LENGTH_SHORT).show();
            redirectToDashboard();
        } else {
            // Invalid role
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

    private void deleteCurrentUser(String email,String password) {
        // Get the current user
        FirebaseUser currentUser = fAuth.getCurrentUser();

        if (currentUser != null) {
            // Re-authenticate the user before deleting the account
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            currentUser.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        // User re-authenticated successfully
                        // Now proceed to delete the user
                        currentUser.delete()
                                .addOnSuccessListener(aVoid1 -> {
                                    // User deleted successfully from Authentication
                                    // Now, you may want to perform additional cleanup or actions
                                    Toast.makeText(Login.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                    // Redirect to login or any other desired screen
                                })
                                .addOnFailureListener(e -> {
                                    // Handle errors
                                    // e.g., FirebaseAuthInvalidUserException, etc.
                                    Toast.makeText(Login.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                });
                    })
                    .addOnFailureListener(e -> {
                        // Handle re-authentication failure
                        Toast.makeText(Login.this, "Re-authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    });
        } else {
            // No user is currently signed in
            Toast.makeText(Login.this, "No user is currently signed in", Toast.LENGTH_SHORT).show();
        }
    }
}
