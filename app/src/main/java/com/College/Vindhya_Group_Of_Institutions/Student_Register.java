package com.College.Vindhya_Group_Of_Institutions;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Student_Register extends AppCompatActivity {

    private EditText firstName, lastName, email, password, con_password, phone, rollNumber, enrollNumber, fatherName;
    private Spinner spinnerDepart, spinnerYear;
    private Button register;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestFullScreen();
        setContentView(R.layout.student_register);

        initializeViews();
        initializeFirebase();
        setSpinnerData();

        register.setOnClickListener(v -> {
            ProgressDialog progressDialog = createProgressDialog();
            progressDialog.show();

            if (validateUserInput(progressDialog)) {
                registerUser(progressDialog);
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

        ArrayList<String> years = new ArrayList<>();
        years.add("1st_Year");
        years.add("2nd_Year");
        years.add("3rd_Year");

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerYear.setAdapter(yearAdapter);
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    private boolean validateUserInput(ProgressDialog progressDialog) {
        String mFirstName = getTextFromField(firstName);
        String mLastName = getTextFromField(lastName);
        String mEmail = getTextFromField(email);
        String mPassword = getTextFromField(password);
        String conPassword = getTextFromField(con_password);
        String mPhone = getTextFromField(phone);
        String mRollNumber = getTextFromField(rollNumber);
        String mEnrollment = getTextFromField(enrollNumber);

        if (TextUtils.isEmpty(mFirstName) || TextUtils.isEmpty(mLastName) || TextUtils.isEmpty(mEmail) ||
                TextUtils.isEmpty(mPassword) || TextUtils.isEmpty(conPassword) || TextUtils.isEmpty(mPhone) ||
                TextUtils.isEmpty(mRollNumber) || TextUtils.isEmpty(mEnrollment)) {
            showErrorAndDismiss("All fields are required!", progressDialog);
            return false;
        }

        if (mPassword.length() < 6) {
            showErrorAndDismiss("Password must be 6 letters or more!", progressDialog);
            showError(password, "Password must be 6 letters or more!");
            return false;
        }

        if (!mPassword.equals(conPassword)) {
            showErrorAndDismiss("Password does not match!", progressDialog);
            showError(con_password, "Password does not match!");
            return false;
        }

        if (!isValidIndianPhoneNumber(mPhone)) {
            showErrorAndDismiss("Enter a valid 10-digit Indian phone number!", progressDialog);
            showError(phone, "Enter a valid 10-digit Indian phone number!");
            return false;
        }

        if (mRollNumber.length() != 10) {
            showErrorAndDismiss("Enter Correct RollNumber!", progressDialog);
            showError(rollNumber, "Enter Correct RollNumber!");
            return false;
        }

        if (mEnrollment.length() != 12) {
            showErrorAndDismiss("Enter Correct EnrollmentNumber!", progressDialog);
            showError(enrollNumber, "Enter Correct EnrollmentNumber!");
            return false;
        }

        return true;
    }

    private boolean isValidIndianPhoneNumber(String phoneNumber) {
        String phoneRegex = "^[6789]\\d{9}$";
        return Pattern.matches(phoneRegex, phoneNumber);
    }

    private void registerUser(ProgressDialog progressDialog) {
        String mFirstName = getTextFromField(firstName);
        String mLastName = getTextFromField(lastName);
        String mEmail = getTextFromField(email);
        String mPassword = getTextFromField(password);
        String mYear = spinnerYear.getSelectedItem().toString();
        String mDepartment = spinnerDepart.getSelectedItem().toString();
        String mPhone = getTextFromField(phone);
        String mRollNumber = getTextFromField(rollNumber);
        String mEnrollment = getTextFromField(enrollNumber);
        String mFather = getTextFromField(fatherName);

        fAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(task -> {
            DocumentReference dataRef = fireStore.collection("Students").document(mEmail);
            Map<String, Object> user = new HashMap<>();
            user.put("FirstName", mFirstName);
            user.put("LastName", mLastName);
            user.put("Department", mDepartment);
            user.put("Year", mYear);
            user.put("Phone", mPhone);
            user.put("RollNumber", mRollNumber);
            user.put("EnrollmentNumber", mEnrollment);
            user.put("FatherName", mFather);
            user.put("Password", mPassword);
            user.put("Role", "Student");

            dataRef.set(user).addOnCompleteListener(task1 -> {
                showToastAndDismiss("User Profile Created Successfully.", progressDialog);
                clearTextFields();
            }).addOnFailureListener(e -> {
                showToastAndDismiss("Error! " + e.getMessage(), progressDialog);
                handleRegistrationFailure(mEmail);
            });
        }).addOnFailureListener(e -> showToastAndDismiss("Error! " + e.getMessage(), progressDialog));
    }

    private String getTextFromField(EditText field) {
        return field.getText().toString().trim();
    }

    private void showToastAndDismiss(String message, ProgressDialog progressDialog) {
        progressDialog.dismiss();
        showToast(message);

    }

    private void showErrorAndDismiss(String errorMessage, ProgressDialog progressDialog) {
        showToast(errorMessage);
        progressDialog.dismiss();
    }

    private void showToast(String message) {
        Toast.makeText(Student_Register.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showError(EditText field, String errorMessage) {
        field.setError(errorMessage);
        field.requestFocus();
    }

    private void clearTextFields() {
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

    private void handleRegistrationFailure(String email) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    showToast("User deleted successfully.");
                } else {
                    showToast("Failed to delete user.");
                }
            });
        }
    }
}
