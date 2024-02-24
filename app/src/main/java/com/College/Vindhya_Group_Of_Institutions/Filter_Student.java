package com.College.Vindhya_Group_Of_Institutions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Filter_Student extends AppCompatActivity {
    private Spinner spinnerDepart, spinnerYear;
    Button fetch;
    String department,year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_student);

        spinnerDepart = findViewById(R.id.Department);
        spinnerYear = findViewById(R.id.year);
        fetch = findViewById(R.id.btnFetch);

        setSpinnerData();

        fetch.setOnClickListener(v -> {

            department = spinnerDepart.getSelectedItem().toString();
            year = spinnerYear.getSelectedItem().toString();
            Intent intent = new Intent(Filter_Student.this, Update_Student.class);
            intent.putExtra("Department",department);
            intent.putExtra("Year",year);
            startActivity(intent);
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