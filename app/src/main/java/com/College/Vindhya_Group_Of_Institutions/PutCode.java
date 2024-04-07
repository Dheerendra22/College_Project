package com.College.Vindhya_Group_Of_Institutions;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PutCode extends AppCompatActivity {

    EditText code;
    Button setCode,DeleteCode;

    FirebaseFirestore firestore;
    SharedPreferences sharedPreferences;
    String email;
    private Progress_Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_put_code);
        code = findViewById(R.id.edtCode);
        setCode = findViewById(R.id.btnSet);
        DeleteCode = findViewById(R.id.btnDelete);
        firestore = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences("Profile", MODE_PRIVATE);
        progressDialog = new Progress_Dialog(PutCode.this);
        progressDialog.setMessage("Please Wait...");


        setCode.setOnClickListener(v -> setCodeInFireStore());

        DeleteCode.setOnClickListener(v -> {
            if(code!=null)
                deleteCode();
        });

    }

    private void deleteCode() {
        progressDialog.show();

        Map<String, Object> data = new HashMap<>();
        data.put("Code", "");

        firestore.collection("Faculty").document(email)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    // Update successful
                    Toast.makeText(this, "Code Deleted Successfully.", Toast.LENGTH_SHORT).show();
                    code.setText("");
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    // Handle errors
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void setCodeInFireStore() {
        progressDialog.show();
        email = sharedPreferences.getString("Email","");
        String uniqueCode = code.getText().toString().trim();
        // Create a Map with the new field
        Map<String, Object> data = new HashMap<>();
        data.put("Code", uniqueCode);

        firestore.collection("Faculty").document(email)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    // Update successful
                    Toast.makeText(this, "Code Added Successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    // Handle errors
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}