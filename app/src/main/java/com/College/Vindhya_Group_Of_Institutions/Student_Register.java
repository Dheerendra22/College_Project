package com.College.Vindhya_Group_Of_Institutions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Student_Register extends AppCompatActivity {

    EditText firstName, lastName, email, password, con_password, phone, rollNumber, enrollNumber, fatherName;
    Spinner spinnerDepart, spinnerYear;
    Button register;
    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_register);

        spinnerDepart = findViewById(R.id.Department);
        spinnerYear = findViewById(R.id.Year);
        register = findViewById(R.id.btnRegister);
        firstName = findViewById(R.id.edtFirstName);
        lastName = findViewById(R.id.edtLastName);
        email = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPassword);
        con_password = findViewById(R.id.edtConPassword);
        phone = findViewById(R.id.edtPhone);
        rollNumber = findViewById(R.id.edtRoll);
        enrollNumber = findViewById(R.id.edtEnroll);
        fatherName = findViewById(R.id.edtFatherName);

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("BCA");
        arrayList.add("BCOM");
        arrayList.add("BSC");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerDepart.setAdapter(adapter);

        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList2.add("1st_Year");
        arrayList2.add("2nd_Year");
        arrayList2.add("3rd_Year");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList2);
        adapter2.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerYear.setAdapter(adapter2);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressDialog progressDialog = new ProgressDialog(Student_Register.this);
                progressDialog.setMessage("Registering...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String mFirstName = firstName.getText().toString().trim();
                String mLastName = lastName.getText().toString();
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                String conPassword = con_password.getText().toString().trim();
                String mYear = spinnerYear.getSelectedItem().toString();
                String mDepartment = spinnerDepart.getSelectedItem().toString();
                String mPhone = phone.getText().toString().trim();
                String mEnrollment = enrollNumber.getText().toString().trim();
                String mRollNumber = rollNumber.getText().toString().trim();
                String mFather = fatherName.getText().toString();
                String countryCode = "91";

                if (TextUtils.isEmpty(mFirstName) || TextUtils.isEmpty(mLastName) || TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword) ||
                        TextUtils.isEmpty(conPassword) || TextUtils.isEmpty(mPhone)) {
                    progressDialog.dismiss();
                    Toast.makeText(Student_Register.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mPassword.length() < 6) {
                    progressDialog.dismiss();
                    password.setError("Password must be 6 letters or more!");
                    password.requestFocus();
                    return;
                }

                if (!mPassword.equals(conPassword)) {
                    progressDialog.dismiss();
                    con_password.setError("Password does not match!");
                    password.requestFocus();
                    return;
                }

                if (mPhone.length() != 10) {
                    progressDialog.dismiss();
                    // The phone number is not valid, show an error message
                    phone.setError("Enter a valid phone number for India!");
                    phone.requestFocus();
                    return;
                }
                if (mRollNumber.length() != 10) {
                    progressDialog.dismiss();
                    rollNumber.setError("Enter Correct RollNumber!");
                    rollNumber.requestFocus();
                    return;
                }
                if (mEnrollment.length() != 12) {
                    progressDialog.dismiss();
                    rollNumber.setError("Enter Correct EnrollmentNumber!");
                    rollNumber.requestFocus();
                    return;
                }

                fAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(task -> {
                    DocumentReference dataRef = fireStore.collection("Students").document(mEmail);
                    Map<String, Object> user1 = new HashMap<>();
                    user1.put("FirstName", mFirstName);
                    user1.put("LastName", mLastName);
                    user1.put("Department", mDepartment);
                    user1.put("Year", mYear);
                    user1.put("Phone", mPhone);
                    user1.put("RollNumber", mRollNumber);
                    user1.put("EnrollmentNumber", mEnrollment);
                    user1.put("FatherName", mFather);
                    user1.put("Role","Student");

                    dataRef.set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Student_Register.this, "User Profile Created Successfully.", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();

                            firstName.setText("");
                            lastName.setText("");
                            email.setText("");
                            password.setText("");
                            con_password.setText("");
                            phone.setText("");
                            rollNumber.setText("");
                            enrollNumber.setText("");
                            fatherName.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Student_Register.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Student_Register.this, "User deleted successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Student_Register.this, "Failed to delete user.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Student_Register.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }
}
