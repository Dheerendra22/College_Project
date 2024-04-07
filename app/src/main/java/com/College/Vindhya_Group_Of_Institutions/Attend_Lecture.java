package com.College.Vindhya_Group_Of_Institutions;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Attend_Lecture extends AppCompatActivity {

    private Spinner spinnerDepart, spinnerYear, spinnerSub;
    Button attend;
    FirebaseFirestore fireStore;
    SharedPreferences sharedPreferences;
    ArrayList<String> subjectList;
    String email,department,year ,subject;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attend_lecture);

        spinnerDepart = findViewById(R.id.depart);
        spinnerYear = findViewById(R.id.Year);
        attend = findViewById(R.id.Attend);
        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);

        spinnerSub = findViewById(R.id.subject);

        fireStore = FirebaseFirestore.getInstance();


        setSpinnerData();

        setUpSubject();

        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSubject();
            }
        });

    }

    private void updateSubject() {
        department = spinnerDepart.getSelectedItem().toString();
        year = spinnerYear.getSelectedItem().toString();
        subject = spinnerSub.getSelectedItem().toString();

        DocumentReference parentDocRef = fireStore.collection("Track_Sub").document("Departments");
        DocumentReference nestedDocRef = parentDocRef.collection(department).document(year);

        // Get the document snapshot for the nested document (year)
        nestedDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Document exists, check if the field exists
                        if (document.contains(subject)) {
                            // Field exists, proceed with the update
                            nestedDocRef.update(subject, FieldValue.increment(1))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Attend_Lecture.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Attend_Lecture.this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Field does not exist
                            Toast.makeText(Attend_Lecture.this, "Field does not exist in document", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Document does not exist
                        Toast.makeText(Attend_Lecture.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Error getting document
                    Toast.makeText(Attend_Lecture.this, "Error getting document: " + task.getException(), Toast.LENGTH_SHORT).show();
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
        spinnerDepart.setAdapter(departmentAdapter);

        ArrayList<String> years = new ArrayList<>();
        years.add("1st_Year");
        years.add("2nd_Year");
        years.add("3rd_Year");

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerYear.setAdapter(yearAdapter);
    }



    private void setUpSubject() {
        // Set up subject spinner based on the selected teacher
           email = sharedPreferences.getString("Email","");
           subjectList = new ArrayList<>();

        DocumentReference userRef1 = fireStore.collection("Faculty").document(email);

        userRef1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<?> subjects = (List<?>) documentSnapshot.get("SubjectList");

                if (subjects != null && !subjects.isEmpty()) {
                    for (Object subjectObject : subjects) {
                        // Check if each item in the list is a String
                        if (subjectObject instanceof String) {
                            String subject = (String) subjectObject;
                            subjectList.add(subject);
                        }
                    }
                    // Now, you can update your UI or perform any other actions
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(Attend_Lecture.this, android.R.layout.simple_spinner_item, subjectList);
                    subjectAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                    spinnerSub.setAdapter(subjectAdapter);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Attend_Lecture.this, "Subjects list is null or not found" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


}
