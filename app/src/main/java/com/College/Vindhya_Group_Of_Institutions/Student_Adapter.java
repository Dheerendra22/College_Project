package com.College.Vindhya_Group_Of_Institutions;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Student_Adapter extends RecyclerView.Adapter<Student_Adapter.MyViewHolder>{

    ArrayList<Data_Model> dataList ;
    private final StorageReference storageReference;

    public Student_Adapter(ArrayList<Data_Model> dataList) {
        this.dataList = dataList;
        storageReference = FirebaseStorage.getInstance().getReference();

    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.fNAme.setText(dataList.get(position).getFirstName());
        holder.lName.setText(dataList.get(position).getLastName());
        holder.department.setText(dataList.get(position).getDepartment());
        holder.year.setText(dataList.get(position).getYear());
        loadProfileImage(holder.profile,dataList.get(position).getUserId());

        holder.edit.setOnClickListener(v -> {
            final DialogPlus dialogPlus = DialogPlus.newDialog(holder.profile.getContext())
                    .setContentHolder(new ViewHolder(R.layout.edit_student))
                    .setExpanded(true,1100)
                    .create();

            View myView = dialogPlus.getHolderView();
            Spinner spinnerDepart = myView.findViewById(R.id.Department);
            Spinner spinnerYear = myView.findViewById(R.id.Year);
            Button save = myView.findViewById(R.id.btnSave);
            EditText firstName = myView.findViewById(R.id.edtFirstName);
            EditText lastName = myView.findViewById(R.id.edtLastName);
            EditText phone = myView.findViewById(R.id.edtPhone);
            EditText rollNumber = myView.findViewById(R.id.edtRoll);
            EditText enrollNumber = myView.findViewById(R.id.edtEnroll);
            EditText fatherName = myView.findViewById(R.id.edtFatherName);
            EditText email = myView.findViewById(R.id.edtEmail);
            EditText password = myView.findViewById(R.id.edtPassword);

            setSpinnerData(spinnerDepart,spinnerYear,holder.profile.getContext());

            // Get the department from dataList
            String userDepartment = dataList.get(position).getDepartment();
            String userYear = dataList.get(position).getYear();

            // Set the default selection in spinnerDepart
            if (userDepartment != null) {
                int departmentIndex = getIndex(userDepartment, spinnerDepart);
                if (departmentIndex != -1) {
                    spinnerDepart.setSelection(departmentIndex);
                }
            }

            // Set the default selection in spinnerYear
            if (userYear != null) {
                int yearIndex = getIndex(userYear, spinnerYear);
                if (yearIndex != -1) {
                    spinnerYear.setSelection(yearIndex);
                }
            }

            String role = dataList.get(position).getRole();
            if(role.equals("Admin") || role.equals("Teacher")){
                spinnerYear.setVisibility(View.GONE);
            }

            firstName.setText(dataList.get(position).getFirstName());
            lastName.setText(dataList.get(position).getLastName());
            phone.setText(dataList.get(position).getPhone());
            rollNumber.setText(dataList.get(position).getRollNumber());
            enrollNumber.setText(dataList.get(position).getEnrollmentNumber());
            fatherName.setText(dataList.get(position).getFatherName());
            email.setText(dataList.get(position).getEmail());
            password.setText(dataList.get(position).getPassword());
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            email.setEnabled(false);
            password.setEnabled(false);

            dialogPlus.show();

            save.setOnClickListener(v1 -> {
                // Get the updated data from EditText fields
                String updatedFirstName = firstName.getText().toString();
                String updatedLastName = lastName.getText().toString();
                String updatedPhone = phone.getText().toString();
                String updatedRollNumber = rollNumber.getText().toString();
                String updatedEnrollNumber = enrollNumber.getText().toString();
                String updatedFatherName = fatherName.getText().toString();
                String updatedDepartment = spinnerDepart.getSelectedItem().toString();
                String updatedYear = spinnerYear.getSelectedItem().toString();

                // Validate input (add more validation conditions as needed)
                if (TextUtils.isEmpty(updatedFirstName) || TextUtils.isEmpty(updatedLastName) || TextUtils.isEmpty(updatedPhone)
                        || TextUtils.isEmpty(updatedRollNumber) || TextUtils.isEmpty(updatedEnrollNumber) || TextUtils.isEmpty(updatedFatherName)) {
                    // Display an error message or Toast indicating that all fields must be filled
                    Toast.makeText(v1.getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return; // Stop further processing if validation fails
                }

                // Create a Map with the updated data
                Map<String,Object> updatedData = new HashMap<>();
                updatedData.put("FirstName", updatedFirstName);
                updatedData.put("LastName", updatedLastName);
                updatedData.put("Phone", updatedPhone);
                updatedData.put("RollNumber", updatedRollNumber);
                updatedData.put("EnrollmentNumber", updatedEnrollNumber);
                updatedData.put("FatherName", updatedFatherName);
                updatedData.put("Department",updatedDepartment);
                updatedData.put("Year",updatedYear);

                // Validate input
                String validationError = validateInput(updatedFirstName, updatedLastName, updatedPhone, updatedRollNumber, updatedEnrollNumber, updatedFatherName);
                if (!TextUtils.isEmpty(validationError)) {
                    // Display specific error message
                    Toast.makeText(v1.getContext(), validationError, Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get a reference to the FireStore collection and the specific document
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String CollectionName = dataList.get(position).getCollection();
                DocumentReference userRef = db.collection(CollectionName).document(dataList.get(position).getEmail());

                userRef.update(updatedData).addOnSuccessListener(unused -> {
                    dialogPlus.dismiss();
                    Toast.makeText(v1.getContext(), "Changes Updated Successfully.", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(v1.getContext(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show());

            });



        });




    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fNAme,lName,department ,year;
        ImageView profile,edit,delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fNAme = itemView.findViewById(R.id.txtFirstName);
            lName = itemView.findViewById(R.id.txtLastName);
            department = itemView.findViewById(R.id.txtDepartment);
            year = itemView.findViewById(R.id.txtYear);
            profile = itemView.findViewById(R.id.imgProfile);
            edit = itemView.findViewById(R.id.imgEdit);
            delete = itemView.findViewById(R.id.imgDelete);


        }
    }

    public void loadProfileImage(ImageView profileImageView, String userid) {
        StorageReference storeRef = storageReference.child("Profile_Images/" + userid);

        storeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            profileImageView.setBackgroundColor(Color.TRANSPARENT);
            Picasso.get().load(uri).into(profileImageView);
        }).addOnFailureListener(e -> {

        });
    }

    private void setSpinnerData(Spinner spinnerDepart, Spinner spinnerYear, Context context) {

        ArrayList<String> departments = new ArrayList<>();
        departments.add("BCA");
        departments.add("BCOM");
        departments.add("BSC");

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerDepart.setAdapter(departmentAdapter);

        ArrayList<String> years = new ArrayList<>();
        years.add("1st_Year");
        years.add("2nd_Year");
        years.add("3rd_Year");

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerYear.setAdapter(yearAdapter);
    }

    private String validateInput(String firstName, String lastName, String phone, String rollNumber, String enrollNumber, String fatherName) {
        if (TextUtils.isEmpty(firstName)) {
            return "Please enter First Name.";
        }
        if (TextUtils.isEmpty(lastName)) {
            return "Please enter Last Name.";
        }
        if (TextUtils.isEmpty(phone)) {
            return "Please enter Phone Number.";
        }
        // Add similar conditions for other fields...
        if (TextUtils.isEmpty(rollNumber)) {
            return "Please enter Roll Number.";
        }
        if (TextUtils.isEmpty(enrollNumber)) {
            return "Please enter Enrollment Number.";
        }
        if (TextUtils.isEmpty(fatherName)) {
            return "Please enter Father's Name.";
        }

        // If all validations pass, return an empty string
        return "";
    }

    // Method to find the index of a department in the spinner
    private int getIndex(String department, Spinner spinner) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(department)) {
                return i;
            }
        }
        return -1;
    }

}
