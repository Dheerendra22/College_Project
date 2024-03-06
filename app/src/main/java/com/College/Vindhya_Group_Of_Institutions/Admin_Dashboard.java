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

    ImageView addStudent,addFaculty,profile,updateStudent,updateFaculty, subjAllotment,logout,attendance,timeTable,percent,code,promotion;
    TextView greet,name,role;
    private Profile_Image_Handler profileImageHandler;
    private String userId;

    SharedPreferences sharedPreferences;
    FirebaseAuth fAuth;

    private FirebaseFirestore fireStore;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.admin_dash);

        initializeElement();

        //set Greeting
        greet.setText(getGreeting());

        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            userId = fAuth.getCurrentUser().getUid();
        }

        checkUserExist();

        if(userId!=null)
            profileImageHandler = new Profile_Image_Handler(this, userId);


        setOnClick();

        loadProfileImage();


    }

    private void initializeElement() {

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

    private void setOnClick() {

        logout.setOnClickListener(v -> logout());

        profile.setOnClickListener(v -> profileImageHandler.openGallery());

        addStudent.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Student_Register.class)));

        addFaculty.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Faculty_Register.class)));

        updateStudent.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, Filter_Student.class)));

        updateFaculty.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, Update_Faculty.class)));

        subjAllotment.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, Sub_Allot.class)));

        attendance.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),Teacher_Attendance.class)));

        timeTable.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, TimeTable.class)));

        code.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, PutCode.class)));

        percent.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ExcelFile.class)));

        promotion.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, Promotion.class)));
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
        } else if (hourOfDay < 15) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
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

    private  void checkUserExist(){
        String email = sharedPreferences.getString("Email","");
        String password = sharedPreferences.getString("Password","");

        DocumentReference userRef1 = fireStore.collection("Faculty").document(email);
        userRef1.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document != null && document.exists()) {
                saveToSharedPreferences("FirstName", document.getString("FirstName"));
                saveToSharedPreferences("LastName", document.getString("LastName"));
                saveToSharedPreferences("Phone", document.getString("Phone"));
                saveToSharedPreferences("Department", document.getString("Department"));

                setData();


            }else {

                deleteCurrentUser(email,password);

            }

        }).addOnFailureListener(e -> deleteCurrentUser(email,password));

    }

    private void setData() {
        String FullName = sharedPreferences.getString("FirstName","")+" "+sharedPreferences.getString("LastName","");
        name.setText(FullName);
        role.setText(sharedPreferences.getString("Role",""));
    }

    private void deleteCurrentUser(String email, String password) {
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
                                    Toast.makeText(Admin_Dashboard.this, "User deleted successfully", Toast.LENGTH_SHORT).show();

                                    // Redirect to login or any other desired screen
                                    deleteSharedPreferences();
                                    startActivity(new Intent(getApplicationContext(), Login.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle errors
                                    // e.g., FirebaseAuthInvalidUserException, etc.
                                    Toast.makeText(Admin_Dashboard.this, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    logout();
                                });
                    })
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



}
