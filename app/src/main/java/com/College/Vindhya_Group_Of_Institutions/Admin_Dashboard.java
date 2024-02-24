package com.College.Vindhya_Group_Of_Institutions;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class Admin_Dashboard extends AppCompatActivity {

    ImageView addStudent,addFaculty,profile,updateStudent,updateFaculty;
    TextView greet,logout;
    private Profile_Image_Handler profileImageHandler;
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.admin_dash);
        addStudent = findViewById(R.id.imgStudent);
        addFaculty = findViewById(R.id.imgFaculty);
        profile = findViewById(R.id.imgProfile);
        greet = findViewById(R.id.txtGreet);
        updateStudent = findViewById(R.id.imgUpdateStudent);
        updateFaculty = findViewById(R.id.imgUpdateFaculty);
        logout = findViewById(R.id.logout);

        //set Greeting
        greet.setText(getGreeting());

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            userId = fAuth.getCurrentUser().getUid();
        }


        if(userId!=null)
            profileImageHandler = new Profile_Image_Handler(this, userId);


        loadProfileImage();

        logout.setOnClickListener(v -> logout());


        profile.setOnClickListener(v -> profileImageHandler.openGallery());

        addStudent.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Student_Register.class)));

        addFaculty.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Faculty_Register.class)));

        updateStudent.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, Filter_Student.class)));

        updateFaculty.setOnClickListener(v -> startActivity(new Intent(Admin_Dashboard.this, Update_Faculty.class)));

    }

    private void logout() {
            FirebaseAuth.getInstance().signOut();
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

}
