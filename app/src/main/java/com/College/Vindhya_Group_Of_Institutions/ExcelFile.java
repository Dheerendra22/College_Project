package com.College.Vindhya_Group_Of_Institutions;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class ExcelFile extends AppCompatActivity {

    private Spinner spinnerDepart, spinnerYear;
   Button fetch;
    String department,year;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel_file);
        spinnerDepart = findViewById(R.id.Department);
        spinnerYear = findViewById(R.id.Year);
        fetch = findViewById(R.id.fetch);

        setSpinnerData();

        fetch.setOnClickListener(v -> fetchDataFromSheet());


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

    private void fetchDataFromSheet() {
        // Replace "YOUR_APP_SCRIPT_URL" with your Google Apps Script web app URL
        String url = "https://script.google.com/macros/s/AKfycbypopJk-HkpWFcTnIxMbACTDpFKk_o9dwwqLdFVJRaCk5Fi1DoRgicrqch5hHOLqur3KA/exec";

        // Creating a JsonArrayRequest to fetch the JSON array response
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                response -> {

                    // Handle the JSON array response
                    Toast.makeText(getApplicationContext(), "Fetching data Successfully.", Toast.LENGTH_SHORT).show();
                    department = spinnerDepart.getSelectedItem().toString();
                    year = spinnerYear.getSelectedItem().toString();
                    FirestoreHelper.storeArrayListInFirestore(response,"Attendance",department+"_"+year,getApplicationContext());

                },
                error -> Toast.makeText(getApplicationContext(), "Error fetching data", Toast.LENGTH_LONG).show());

        // Creating a RequestQueue and adding the request to the queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }


}
