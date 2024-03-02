package com.College.Vindhya_Group_Of_Institutions;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TimeTable extends AppCompatActivity {
    private Spinner spinnerDepart, spinnerYear;
    Button upload;
    String department,year;
    ImageView timeTable;
    private Time_Table_Handler timeTableImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_time_table);

        spinnerDepart = findViewById(R.id.Department);
        spinnerYear = findViewById(R.id.Year);
        timeTable = findViewById(R.id.imgPhoto);
         upload = findViewById(R.id.buttonUpload);



        timeTableImg = new Time_Table_Handler(this);


        setSpinnerData();

        timeTable.setOnClickListener(v -> timeTableImg.openGallery());

        upload.setOnClickListener(v -> {
           department = spinnerDepart.getSelectedItem().toString();
           year = spinnerYear.getSelectedItem().toString();
                // Check if an image is selected
            if (timeTableImg.isImageSelected()) {
                // Image is selected, proceed with the upload
                timeTableImg.uploadImageToFirebase(TimeTable.this, department, year);
            } else {
                // No image selected, display a message
                Toast.makeText(TimeTable.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void setSpinnerData() {
        ArrayList<String> departments = new ArrayList<>();
        departments.add("BCA");
        departments.add("BCOM");
        departments.add("BSC");

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerDepart.setAdapter(departmentAdapter);

        ArrayList<String> years = new ArrayList<>();
        years.add("1st_Year");
        years.add("2nd_Year");
        years.add("3rd_Year");

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerYear.setAdapter(yearAdapter);
    }
}