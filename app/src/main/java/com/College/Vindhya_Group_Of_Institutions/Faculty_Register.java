package com.College.Vindhya_Group_Of_Institutions;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    private Spinner spinnerDepart, spinnerRole;
    private EditText firstName, lastName, email, password, con_password, phone;
    private Button register;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;
    private Progress_Dialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestFullScreen();
        setContentView(R.layout.faculty_register);

        initializeViews();
        initializeFirebase();
        setSpinnerData();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                registerUser();
            }
        });
    }

    private void requestFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initializeViews() {
        spinnerDepart = findViewById(R.id.Department);
        spinnerRole = findViewById(R.id.Role);
        register = findViewById(R.id.btnRegister);
        firstName = findViewById(R.id.edtFirstName);
        lastName = findViewById(R.id.edtLastName);
        email = findViewById(R.id.edtEmail);
        password = findViewById(R.id.edtPassword);
        con_password = findViewById(R.id.edtConPassword);
        phone = findViewById(R.id.edtPhone);
        progressDialog = new Progress_Dialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
    }

    private void initializeFirebase() {
        fAuth = FirebaseAuth.getInstance();
        fireStore = FirebaseFirestore.getInstance();
    }

    private void setSpinnerData() {
        ArrayList<String> departments = new ArrayList<>();
        departments.add("BCA");
        departments.add("BCOM");
        departments.add("BSC");

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerDepart.setAdapter(departmentAdapter);

        ArrayList<String> roles = new ArrayList<>();
        roles.add("Admin");
        roles.add("Teacher");

        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        roleAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerRole.setAdapter(roleAdapter);
    }

    private void registerUser() {
        String mFirstName = getTextFromField(firstName);
        String mLastName = getTextFromField(lastName);
        String mEmail = getTextFromField(email);
        String mPassword = getTextFromField(password);
        String conPassword = getTextFromField(con_password);
        String mDepartment = spinnerDepart.getSelectedItem().toString();
        String role = spinnerRole.getSelectedItem().toString();
        String mPhone = getTextFromField(phone);

        if (validateInputs(mFirstName, mLastName, mEmail, mPassword, conPassword, mPhone)) {
            fAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    saveUserDataToFireStore(mEmail, mFirstName, mLastName, mDepartment, mPhone, mPassword, role);
                } else {
                    handleRegistrationFailure(task.toString());
                }
            });
        } else {
            progressDialog.dismiss();
        }
    }

    private String getTextFromField(EditText field) {
        return field.getText().toString().trim();
    }

    private boolean validateInputs(String mFirstName, String mLastName, String mEmail, String mPassword, String conPassword, String mPhone) {
        if (TextUtils.isEmpty(mFirstName) || TextUtils.isEmpty(mLastName) || TextUtils.isEmpty(mEmail) ||
                TextUtils.isEmpty(mPassword) || TextUtils.isEmpty(conPassword) || TextUtils.isEmpty(mPhone)) {
            showToast("All fields are required!");
            return false;
        }
        if (mPassword.length() < 6) {
            showError(password, "Password must be 6 letters or more!");
            return false;
        }
        if (!mPassword.equals(conPassword)) {
            showError(con_password, "Password does not match!");
            return false;
        }
        if (mPhone.length() != 10 || !isValidIndianPhoneNumber(mPhone)) {
            showError(phone, "Enter a valid 10-digit Indian phone number!");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showError(EditText field, String errorMessage) {
        progressDialog.dismiss();
        field.setError(errorMessage);
        field.requestFocus();
    }

    private boolean isValidIndianPhoneNumber(String phoneNumber) {
        // Indian phone numbers start with 6, 7, 8, or 9 and have a total of 10 digits.
        String phoneRegex = "^[6789]\\d{9}$";
        return phoneNumber.matches(phoneRegex);
    }


    private void saveUserDataToFireStore(String userEmail, String userFirstName, String userLastName, String userDepartment,
                                         String userPhone, String userPassword, String userRole) {
        DocumentReference dataRef = fireStore.collection("Faculty").document(userEmail);
        Map<String, Object> user = new HashMap<>();
        user.put("FirstName", userFirstName);
        user.put("LastName", userLastName);
        user.put("Department", userDepartment);
        user.put("Phone", userPhone);
        user.put("Password", userPassword);
        user.put("Role", userRole);

        dataRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                showToast("User Profile Created Successfully.");
                clearTextFields();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                showToast("Error! " + e.getMessage());
                handleRegistrationFailure(userEmail);
            }
        });
    }

    private void handleRegistrationFailure(String userEmail) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        showToast("User deleted successfully.");
                    } else {
                        showToast("Failed to delete user.");
                    }
                }
            });
        }
    }

    private void clearTextFields() {
        firstName.setText("");
        lastName.setText("");
        email.setText("");
        password.setText("");
        con_password.setText("");
        phone.setText("");
    }
}
