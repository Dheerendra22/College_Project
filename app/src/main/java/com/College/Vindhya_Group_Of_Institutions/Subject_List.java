package com.College.Vindhya_Group_Of_Institutions;

// Subject_List.java

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Subject_List extends AppCompatActivity {

    private LinearLayout editTextContainer;
    private Button addButton, saveButton;
    private ArrayList<EditText> editTextList;
    private ArrayList<String> subjectList;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        editTextContainer = findViewById(R.id.editTextContainer);
        addButton = findViewById(R.id.addButton);
        saveButton = findViewById(R.id.saveButton);

        editTextList = new ArrayList<>();

        intent = getIntent();
        if (intent != null) {
            // Retrieve subjectList from Intent
            subjectList = intent.getStringArrayListExtra("SubjectList");

            // Check if subjectList is not null before using it
            if (subjectList != null) {
                // Display EditText views for each item in subjectList
                for (String subject : subjectList) {
                    addNewEditText(subject);
                }
            } else {
                // Handle the case where subjectList is null (show a message, set default values, etc.)
                Toast.makeText(this, "No subjects available", Toast.LENGTH_SHORT).show();
            }
        }

        addButton.setOnClickListener(v -> addNewEditText(""));

        saveButton.setOnClickListener(v -> saveEditTextContents());
    }

    private void saveEditTextContents() {
        if(subjectList!=null)
            subjectList.clear();

        for (int i = 0; i < editTextList.size(); i++) {
            String updatedText = editTextList.get(i).getText().toString();
            subjectList.add(updatedText);
        }

        // Now 'subjectList' contains the updated content
        // You can use it as needed, for example, pass it back to the calling activity

        saveToFireStore(subjectList);


    }

    private void saveToFireStore(ArrayList<String> subjectList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = intent.getStringExtra("email");
        assert email != null;
        DocumentReference userRef = db.collection("Faculty").document(email);
        Map<String,Object> updatedData = new HashMap<>();
        updatedData.put("SubjectList", subjectList);
        userRef.update(updatedData).addOnSuccessListener(unused -> {
            Toast.makeText(Subject_List.this, "Updated Successfully.", Toast.LENGTH_SHORT).show();
            finish();

        }).addOnFailureListener(e -> Toast.makeText(Subject_List.this, e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void addNewEditText(String initialText) {
        EditText newEditText = new EditText(this);
        newEditText.setText(initialText);
        editTextList.add(newEditText);

        // Create a delete button for each EditText
        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> removeEditText(newEditText, deleteButton));

        // Add both the EditText and the delete button to the container
        editTextContainer.addView(newEditText);
        editTextContainer.addView(deleteButton);
    }

    private void removeEditText(EditText editText, Button deleteButton) {
        editTextList.remove(editText);
        editTextContainer.removeView(editText);
        editTextContainer.removeView(deleteButton);
    }
}
