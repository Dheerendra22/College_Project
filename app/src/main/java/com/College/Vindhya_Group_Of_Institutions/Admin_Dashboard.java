package com.College.Vindhya_Group_Of_Institutions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Admin_Dashboard extends AppCompatActivity {

    ImageView addStudent,addFaculty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dash);
        addStudent = findViewById(R.id.imgStudent);
        addFaculty = findViewById(R.id.imgFaculty);

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Student_Register.class));
            }
        });

        addFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Faculty_Register.class));
            }
        });

    }
}
