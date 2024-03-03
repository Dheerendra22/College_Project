package com.College.Vindhya_Group_Of_Institutions;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Attendance extends AppCompatActivity {
    TextView name,depart,year;
    Spinner lecture,teacher,subject;
    RatingBar rb;
    EditText uniqueCode;
    Button present;

    ArrayList<String> lectureList,teacherList,subjectList;
    SharedPreferences sharedPreferences;
    private Progress_Dialog progressDialog;
    FirebaseFirestore fireStore;
    String FullName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        lecture = findViewById(R.id.lecture);
        teacher = findViewById(R.id.teacher);
        subject = findViewById(R.id.subject);

        name = findViewById(R.id.name);
        depart = findViewById(R.id.department);
        year = findViewById(R.id.year);
        uniqueCode = findViewById(R.id.uniqueCode);
        present = findViewById(R.id.btnPresent);
        rb = findViewById(R.id.ratingBar);

        progressDialog = new Progress_Dialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);


        FullName = sharedPreferences.getString("FirstName","")+" "+sharedPreferences.getString("LastName","");
        name.setText(FullName);
        depart.setText(sharedPreferences.getString("Department",""));
        year.setText(sharedPreferences.getString("Year",""));

        setUpLecture();
        setUpTeacher();

        teacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Call setUpSubject when a teacher is selected
                setUpSubject();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });

        present.setOnClickListener(v -> sendAttendance());


    }

    private void sendAttendance() {

        progressDialog.show();

        String depart = sharedPreferences.getString("Department","");
        String year = sharedPreferences.getString("Year","");
        String selectedLecture = lecture.getSelectedItem().toString();
        String selectedTeacher = teacher.getSelectedItem().toString();
        String selectedSubject = subject.getSelectedItem().toString();
        String uniqueCodeValue = uniqueCode.getText().toString();

        String rating = String.valueOf(rb.getRating()); // Retrieve rating from RatingBar

        if (TextUtils.isEmpty(uniqueCodeValue) || rb.getRating() == 0.0) {

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please fill in all the required fields", Toast.LENGTH_LONG).show();
            return; // Stop execution if any data is null or empty
        }

        if (!LectureTimeValidator.isLectureTimeValid(selectedLecture)) {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Attendance can only be submitted during the lecture period", Toast.LENGTH_LONG).show();
            return;
        }


        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyivh6UoHQM5aD9dwgGacKgjVYZLaykJcOCsh9QIFTw1I_Dhz4MslKN3h_LBxLoV__sNg/exec",
                response -> {

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    finish();

                },
                error -> {

                }
        ) {
            @Override
            protected Map<String,String> getParams() {
                Map<String, String> value = new HashMap<>();

                //here we pass params
                value.put("action","Student");
                value.put("sheetName",depart);
                value.put("Name",FullName);
                value.put("Department",depart);
                value.put("Year",year);
                value.put("Lecture",selectedLecture);
                value.put("Teacher",selectedTeacher);
                value.put("Subject",selectedSubject);
                value.put("Code",uniqueCodeValue);
                value.put("Rating", rating);
                return value;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);



    }

    private void setUpLecture() {
        lectureList = new ArrayList<>();
        lectureList.add("Lecture 1");
        lectureList.add("Lecture 2");
        lectureList.add("Lecture 3");
        lectureList.add("Lecture 4");
        lectureList.add("Lecture 5");
        lectureList.add("Lecture 6");

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lectureList);
        departmentAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        lecture.setAdapter(departmentAdapter);

    }

    private void setUpTeacher() {
        fireStore = FirebaseFirestore.getInstance();

        teacherList = new ArrayList<>();

        // Reference to the "Faculty" collection
        CollectionReference facultyCollection = fireStore.collection("Faculty");

        facultyCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                // Assuming the faculty names are stored in a field called "FirstName"
                String firstName = documentSnapshot.getString("FirstName");
//                String lastName = documentSnapshot.getString("LastName");

                if (firstName != null) {
                    teacherList.add(firstName);
                }
            }

            ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherList);
            teacherAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
            teacher.setAdapter(teacherAdapter);



        }).addOnFailureListener(e -> {
            // Handle the failure (e.g., display an error message)
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        });

    }

    private void setUpSubject() {
        if (teacher != null && teacher.getSelectedItem() != null) {
            String fName = teacher.getSelectedItem().toString();
            subjectList = new ArrayList<>();

            CollectionReference facultyCollection = fireStore.collection("Faculty");

            // Set a whereEqualTo condition for the faculty name
            Query query = facultyCollection.whereEqualTo("FirstName", fName).limit(1);

            query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Get the reference to the first matching document
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

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
                        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(Attendance.this, android.R.layout.simple_spinner_item, subjectList);
                        subjectAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                        subject.setAdapter(subjectAdapter);

                    }else {
                        // Handle the case where the "Subjects" field is null or not found
                        Toast.makeText(Attendance.this, "Subjects list is null or not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle the case where no documents match the query
                    Toast.makeText(Attendance.this, "No matching document found", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                // Handle failures
                Toast.makeText(Attendance.this, e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    }


}