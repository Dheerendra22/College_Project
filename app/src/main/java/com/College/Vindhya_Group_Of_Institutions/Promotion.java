package com.College.Vindhya_Group_Of_Institutions;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Promotion extends AppCompatActivity {

    private Spinner Depart, FromYear,ToYear;
    Button promote;
    String department,fromYear , toYear;
    private Progress_Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);

        Depart = findViewById(R.id.department);
        FromYear = findViewById(R.id.fromYear);
        ToYear = findViewById(R.id.toYear);
        promote = findViewById(R.id.btnPromote);
        progressDialog = new Progress_Dialog(Promotion.this);
        progressDialog.setMessage("Please Wait...");


        setSpinnerData();

        promote.setOnClickListener(v -> {
            progressDialog.show();
            department = Depart.getSelectedItem().toString();
            fromYear = FromYear.getSelectedItem().toString();
            toYear = ToYear.getSelectedItem().toString();

            if (!fromYear.equals(toYear)){
                promoteStudent();
            }else {
                progressDialog.dismiss();
                Toast.makeText(Promotion.this, "Your Selection is wrong!", Toast.LENGTH_SHORT).show();
            }


        });


    }

    private void promoteStudent() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference studentsCollection = firestore.collection("Students");

        // Query students with Department == "BCA"
        Query StudentsQuery = studentsCollection.whereEqualTo("Department", department).whereEqualTo("Year",fromYear);

        StudentsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Update the "Year" field to "2nd year"
                    document.getReference().update("Year", toYear);

                    CollectionReference lecturesCollection = document.getReference().collection("Lectures");

                    lecturesCollection.document("Attend").get().addOnCompleteListener(attendTask -> {
                        if (attendTask.isSuccessful()) {
                            DocumentSnapshot attendDocument = attendTask.getResult();
                            if (attendDocument.exists()) {
                                Map<String, Object> fields = attendDocument.getData();
                                // Update each field to null
                                Map<String, Object> updateData = new HashMap<>();
                                assert fields != null;
                                for (String key : fields.keySet()) {
                                    updateData.put(key, FieldValue.delete());
                                }
                                // Update the "Attend" document with the null values
                                attendDocument.getReference().update(updateData);
                            }
                        }
                    });
                }
                if (task.getResult().isEmpty()) {
                    progressDialog.dismiss();
                    Toast.makeText(Promotion.this, "No students found for the selected criteria.", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Promotion.this, "Students promoted successfully.", Toast.LENGTH_SHORT).show();
                }
                // Handle the completion here if needed
            } else {
                // Handle errors
                Exception exception = task.getException();
                if (exception != null) {
                    progressDialog.dismiss();
                    Toast.makeText(Promotion.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
        Depart.setAdapter(departmentAdapter);

        ArrayList<String> years = new ArrayList<>();
        years.add("1st_Year");
        years.add("2nd_Year");
        years.add("3rd_Year");

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);

        FromYear.setAdapter(yearAdapter);

        ToYear.setAdapter(yearAdapter);
    }
}