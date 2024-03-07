package com.College.Vindhya_Group_Of_Institutions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class Admin_Dashboard extends AppCompatActivity {

    // UI elements
    ImageView addStudent, addFaculty, profile, updateStudent, updateFaculty, subjAllotment, logout, attendance, timeTable, percent, code, promotion;
    TextView greet, name, role;

    // Helper classes
    private Profile_Image_Handler profileImageHandler;
    private String userId;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set full-screen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set the layout
        setContentView(R.layout.admin_dash);

        // Initialize UI elements
        initializeElements();

        // Set greeting
        greet.setText(getGreeting());

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        // Check if a user is logged in
        if (fAuth.getCurrentUser() != null) {
            userId = fAuth.getCurrentUser().getUid();
        }

        // Check user existence and load profile image
        checkUserExist();
        if (userId != null)
            profileImageHandler = new Profile_Image_Handler(this, userId);

        // Set click listeners for various buttons
        setOnClick();
        // Load profile image
        loadProfileImage();
    }

    // Method to initialize UI elements
    private void initializeElements() {

        addStudent = findViewById(R.id.imgStudent);
        addFaculty = findViewById(R.id.imgFaculty);
        profile = findViewById(R.id.imgProfile);
        greet = findViewById(R.id.txtGreet);
        updateStudent = findViewById(R.id.imgUpdateStudent);
        updateFaculty = findViewById(R.id.imgUpdateFaculty);
        subjAllotment = findViewById(R.id.subAllot);
        logout = findViewById(R.id.logout);
        name = findViewById(R.id.txtName);
        role = findViewById(R.id.txtRole);
        attendance = findViewById(R.id.imgAttendance);
        timeTable = findViewById(R.id.timeTable);
        percent = findViewById(R.id.percent);
        code = findViewById(R.id.imgCode);
        promotion = findViewById(R.id.promote);
        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
    }

    // Method to set click listeners for buttons
    private void setOnClick() {

            logout.setOnClickListener(v -> logout());
            profile.setOnClickListener(v -> profileImageHandler.openGallery());
            addStudent.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Student_Register.class)));
            addFaculty.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Faculty_Register.class)));
            updateStudent.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, Filter_Student.class)));
            updateFaculty.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, Update_Faculty.class)));
            subjAllotment.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, Sub_Allot.class)));
            attendance.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Teacher_Attendance.class)));
            timeTable.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, TimeTable.class)));
            code.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, PutCode.class)));
            percent.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ExcelFile.class)));
            promotion.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, Promotion.class)));


    }

    // Method to set up the layout
    private void loadProfileImage() {
        profileImageHandler.loadProfileImage(profile);
    }

    // Method to get a greeting based on the time of the day
    private String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay < 12) {
            return "Good Morning";
        } else if (hourOfDay < 15) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }

    // Method to save data to SharedPreferences
    private void saveToSharedPreferences(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // Method to delete SharedPreferences
    private void deleteSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // Method to check if the user exists in Firestore
    private void checkUserExist() {
        String email = sharedPreferences.getString("Email", "");
        String password = sharedPreferences.getString("Password", "");

        DocumentReference userRef1 = fireStore.collection("Faculty").document(email);
        userRef1.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document != null && document.exists()) {
                saveToSharedPreferences("FirstName", document.getString("FirstName"));
                saveToSharedPreferences("LastName", document.getString("LastName"));
                saveToSharedPreferences("Phone", document.getString("Phone"));
                saveToSharedPreferences("Department", document.getString("Department"));

                // Set user data
                setData();
            } else {
                // Delete the current user if not found in Firestore
                deleteCurrentUser(email, password);
            }
        }).addOnFailureListener(e -> deleteCurrentUser(email, password));
    }

    // Method to set user data to UI
    private void setData() {
        String fullName = sharedPreferences.getString("FirstName", "") + " " + sharedPreferences.getString("LastName", "");
        name.setText(fullName);
        String temp = sharedPreferences.getString("Role", "");
        role.setText(temp);
    }

    // Method to delete the current user from Firebase Authentication
    private void deleteCurrentUser(String email, String password) {
        FirebaseUser currentUser = fAuth.getCurrentUser();

        if (currentUser != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, password);

            currentUser.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> currentUser.delete()
                            .addOnSuccessListener(aVoid1 -> {
                                // User deleted successfully from Authentication
                                Toast.makeText(Admin_Dashboard.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                // Redirect to login or any other desired screen
                                deleteSharedPreferences();
                                startActivity(new Intent(getApplicationContext(), Login.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Handle errors
                                Toast.makeText(Admin_Dashboard.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                logout();
                            }))
                    .addOnFailureListener(e -> {
                        // Handle re-authentication failure
                        Toast.makeText(Admin_Dashboard.this, "Re-authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        deleteSharedPreferences();
                        logout();
                    });
        } else {
            // No user is currently signed in
            Toast.makeText(Admin_Dashboard.this, "No user is currently signed in", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to handle user logout
    private void logout() {
        // Sign out the user from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Clear SharedPreferences
        deleteSharedPreferences();

        // Redirect to the login activity
        startActivity(new Intent(getApplicationContext(), Login.class));

        // Finish the current activity
        finish();
    }

}
