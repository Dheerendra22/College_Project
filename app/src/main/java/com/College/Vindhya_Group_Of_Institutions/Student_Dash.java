package com.College.Vindhya_Group_Of_Institutions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class Student_Dash extends AppCompatActivity {
    TextView greet,name,department,year,role;
    ImageView profile,logout;
    private Profile_Image_Handler profileImageHandler;
    private String userId;
    FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dash);

        profile = findViewById(R.id.imgProfile);

        greet = findViewById(R.id.txtGreet);
        logout = findViewById(R.id.logout);
        name = findViewById(R.id.txtName);
        department = findViewById(R.id.txtDepart);
        year = findViewById(R.id.txtYear);
        role = findViewById(R.id.txtRole);
        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);

        //set Greeting
        greet.setText(getGreeting());

        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            userId = fAuth.getCurrentUser().getUid();
        }

        checkUserExist();
        setData();

        if(userId!=null)
            profileImageHandler = new Profile_Image_Handler(this, userId);

        loadProfileImage();

        profile.setOnClickListener(v -> profileImageHandler.openGallery());

        logout.setOnClickListener(v -> logout());



    }

    private void setData() {
        String FullName = sharedPreferences.getString("FirstName","")+" "+sharedPreferences.getString("LastName","");
        name.setText(FullName);
        department.setText(sharedPreferences.getString("Department",""));
        year.setText(sharedPreferences.getString("Year",""));
        role.setText(sharedPreferences.getString("Role",""));
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        deleteSharedPreferences();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    private void loadProfileImage() {
        profileImageHandler.loadProfileImage(profile);
    }

    private String getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay < 12) {
            return "Good Morning";
        } else if (hourOfDay < 18) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }

    private  void checkUserExist(){
        String email = sharedPreferences.getString("Email","");
        String password = sharedPreferences.getString("Password","");

        DocumentReference userRef1 = fireStore.collection("Students").document(email);
        userRef1.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document != null && document.exists()) {
                saveToSharedPreferences("FirstName", document.getString("FirstName"));
                saveToSharedPreferences("LastName", document.getString("LastName"));
                saveToSharedPreferences("FatherName", document.getString("FatherName"));
                saveToSharedPreferences("EnrollmentNumber", document.getString("EnrollmentNumber"));
                saveToSharedPreferences("RollNumber", document.getString("RollNumber"));
                saveToSharedPreferences("Phone", document.getString("Phone"));
                saveToSharedPreferences("Department", document.getString("Department"));
                saveToSharedPreferences("Year", document.getString("Year"));

            }else {

                deleteCurrentUser(email,password);

            }

        }).addOnFailureListener(e -> deleteCurrentUser(email,password));

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
                                    Toast.makeText(Student_Dash.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                    // Redirect to login or any other desired screen
                                    deleteSharedPreferences();
                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle errors
                                    // e.g., FirebaseAuthInvalidUserException, etc.
                                    Toast.makeText(Student_Dash.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    deleteSharedPreferences();
                                    logout();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Handle re-authentication failure
                        Toast.makeText(Student_Dash.this, "Re-authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        deleteSharedPreferences();
                        logout();
                    });
        } else {
            // No user is currently signed in
            Toast.makeText(Student_Dash.this, "No user is currently signed in", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveToSharedPreferences(String key,String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void deleteSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
