package com.College.Vindhya_Group_Of_Institutions;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Student_TimeTable extends AppCompatActivity {

    ZoomableImageView imgTime;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_time_table);

        Time_Table_Handler timeTableImg = new Time_Table_Handler(this);
        imgTime = findViewById(R.id.imgTime);

        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);

        String department = sharedPreferences.getString("Department","");
        String year = sharedPreferences.getString("Year","");

        timeTableImg.loadProfileImage(imgTime,department,year);



    }
}