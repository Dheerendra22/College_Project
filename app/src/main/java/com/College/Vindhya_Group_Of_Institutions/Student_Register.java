package com.College.Vindhya_Group_Of_Institutions;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class Student_Register extends AppCompatActivity {

    private EditText firstName, lastName, email, password, con_password, phone, rollNumber, enrollNumber, fatherName;
    private Spinner spinnerDepart, spinnerYear;
    private Button register;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fireStore;
    String userId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestFullScreen();
        setContentView(R.layout.student_register);

        initializeViews();
        initializeFirebase();
        setSpinnerData();

        register.setOnClickListener(v -> {
            Progress_Dialog progressDialog = createCustomProgressDialog();
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

    private Progress_Dialog createCustomProgressDialog() {
        Progress_Dialog progressDialog = new Progress_Dialog(this);
        progressDialog.setMessage("Registering...");
        return progressDialog;
    }


    private boolean validateUserInput(Progress_Dialog progressDialog) {
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
            showErrorAndDismiss(progressDialog);
            return false;
        }

        if (mPassword.length() < 6) {
            progressDialog.dismiss();
            showError(password, "Password must be 6 letters or more!");
            return false;
        }

        if (!mPassword.equals(conPassword)) {
            progressDialog.dismiss();
            showError(con_password, "Password does not match!");
            return false;
        }

        if (!isValidIndianPhoneNumber(mPhone)) {
            progressDialog.dismiss();
            showError(phone, "Enter a valid 10-digit Indian phone number!");
            return false;
        }

        if (mRollNumber.length() != 10) {
            progressDialog.dismiss();
            showError(rollNumber, "Enter Correct RollNumber!");
            return false;
        }

        if (mEnrollment.length() != 12) {
            progressDialog.dismiss();
            showError(enrollNumber, "Enter Correct EnrollmentNumber!");
            return false;
        }

        return true;
    }

    private boolean isValidIndianPhoneNumber(String phoneNumber) {
        String phoneRegex = "^[6789]\\d{9}$";
        return Pattern.matches(phoneRegex, phoneNumber);
    }

    private void registerUser(Progress_Dialog progressDialog) {
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
            userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
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
            user.put("Email",mEmail);
            user.put("UserId",userId);
            user.put("Collection","Students");

            dataRef.set(user).addOnCompleteListener(task1 -> {
                createNestedCollection(dataRef);
                showToastAndDismiss("User Profile Created Successfully.", progressDialog);
                clearTextFields();
            }).addOnFailureListener(e -> {
                showToastAndDismiss("Error! " + e.getMessage(), progressDialog);
                clearTextFields();
            });
        }).addOnFailureListener(e -> showToastAndDismiss("Error! " + e.getMessage(), progressDialog));
    }

    private String getTextFromField(EditText field) {
        return field.getText().toString().trim();
    }

    private void showToastAndDismiss(String message, Progress_Dialog progressDialog) {
        progressDialog.dismiss();
        showToast(message);

    }

    private void showErrorAndDismiss(Progress_Dialog progressDialog) {
        progressDialog.dismiss();
        showToast("All fields are required!");

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

    private void createNestedCollection(DocumentReference dRef) {

        // Create a nested collection inside the student document
        CollectionReference nestedCollectionRef = dRef.collection("Lectures"); // Replace "NestedCollectionName" with the desired name of the nested collection

        // Create an empty document named "Attend" inside the nested collection
        nestedCollectionRef.document("Attend").set(new HashMap<>())
                .addOnSuccessListener(aVoid -> {
                    // Document created successfully
                    Toast.makeText(Student_Register.this, "", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Error creating document
                    Toast.makeText(Student_Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}
