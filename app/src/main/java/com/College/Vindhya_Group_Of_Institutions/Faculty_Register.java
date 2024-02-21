package com.College.Vindhya_Group_Of_Institutions;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class Faculty_Register extends AppCompatActivity {
    Spinner spinnerDepart, spinnerRole;
    EditText firstName, lastName, email, password, con_password, phone ;
    Button register;
    FirebaseAuth fAuth;
    FirebaseFirestore fireStore;
    ProgressBar pgBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faculty_register);

        spinnerDepart = findViewById(R.id.Department);
        spinnerRole = findViewById(R.id.Role);
        register = findViewById(R.id.btnRegister);
        firstName = findViewById(R.id.edtFirstName);
        lastName = findViewById(R.id.edtLastName);
        email = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPassword);
        con_password = findViewById(R.id.edtConPassword);
        phone = findViewById(R.id.edtPhone);
        pgBar = findViewById(R.id.pgBar);
        pgBar.setVisibility(View.GONE);

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
        arrayList2.add("Admin");
        arrayList2.add("Teacher");


        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList2);
        adapter2.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerRole.setAdapter(adapter2);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ProgressDialog progressDialog = new ProgressDialog(Faculty_Register.this);
//                progressDialog.setMessage("Registering...");
//                progressDialog.setCancelable(false);
//                progressDialog.show();

                pgBar.setVisibility(View.VISIBLE);

                String mFirstName = firstName.getText().toString().trim();
                String mLastName = lastName.getText().toString().trim();
                String mEmail = email.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                String conPassword = con_password.getText().toString().trim();
                String mDepartment = spinnerDepart.getSelectedItem().toString();
                String role = spinnerRole.getSelectedItem().toString();
                String mPhone = phone.getText().toString().trim();

                if (TextUtils.isEmpty(mFirstName) || TextUtils.isEmpty(mLastName) || TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword) ||
                        TextUtils.isEmpty(conPassword) || TextUtils.isEmpty(mPhone)) {
                    pgBar.setVisibility(View.GONE);
                    Toast.makeText(Faculty_Register.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPassword.length() < 6) {
                    pgBar.setVisibility(View.GONE);
                    password.setError("Password must be 6 letters or more!");
                    password.requestFocus();
                    return;
                }
                if (!mPassword.equals(conPassword)) {
                    pgBar.setVisibility(View.GONE);
                    con_password.setError("Password does not match!");
                    password.requestFocus();
                    return;
                }
                if (mPhone.length() != 10) {
                    pgBar.setVisibility(View.GONE);
                    // The phone number is not valid, show an error message
                    phone.setError("Enter a valid phone number for India!");
                    phone.requestFocus();
                    return;
                }


                fAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(task -> {
                    DocumentReference dataRef = fireStore.collection("Faculty").document(mEmail);
                    Map<String, Object> user1 = new HashMap<>();
                    user1.put("FirstName", mFirstName);
                    user1.put("LastName", mLastName);
                    user1.put("Department", mDepartment);
                    user1.put("Phone", mPhone);
                    user1.put("Password",password);
                    user1.put("Role",role);

                    dataRef.set(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Faculty_Register.this, "User Profile Created Successfully.", Toast.LENGTH_SHORT).show();

                            pgBar.setVisibility(View.GONE);

//                            firstName.setText("");
//                            lastName.setText("");
//                            email.setText("");
//                            password.setText("");
//                            con_password.setText("");
//                            phone.setText("");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Faculty_Register.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            pgBar.setVisibility(View.GONE);

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null) {
                                currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(Faculty_Register.this, "User deleted successfully.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Faculty_Register.this, "Failed to delete user.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(Faculty_Register.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        pgBar.setVisibility(View.GONE);
                    }
                });









            }
        });



    }
}
