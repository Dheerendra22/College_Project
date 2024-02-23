package com.College.Vindhya_Group_Of_Institutions;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class Student_Dash extends AppCompatActivity {
    TextView greet;
    ImageView profile;
    private Profile_Image_Handler profileImageHandler;
    private String userId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dash);

        profile = findViewById(R.id.imgProfile);

        greet = findViewById(R.id.txtGreet);
        //set Greeting
        greet.setText(getGreeting());

        FirebaseAuth fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            userId = fAuth.getCurrentUser().getUid();
        }

        if(userId!=null)
            profileImageHandler = new Profile_Image_Handler(this, userId);

        loadProfileImage();

        profile.setOnClickListener(v -> profileImageHandler.openGallery());


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
