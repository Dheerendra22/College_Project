package com.College.Vindhya_Group_Of_Institutions;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AlertDialog;
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
    private DialogPlus dialogPlus;

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


            String role = dataList.get(position).getRole();

            if(role.equals("Student")){

                dialogPlus = DialogPlus.newDialog(holder.profile.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_student))
                        .setExpanded(true,1200)
                        .create();

                View myView = dialogPlus.getHolderView();

                setupStudentDialog(myView, dataList.get(position));



            } else {

                dialogPlus = DialogPlus.newDialog(holder.profile.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_faculty))
                        .setExpanded(true,1200)
                        .create();

                View myView = dialogPlus.getHolderView();
                setupFacultyDialog(myView, dataList.get(position));


            }


        });

        holder.delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Delete Confirmation")
                    .setMessage("Are you sure you want to delete this user!")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        // Delete data from Firebase Storage
                        deleteDataFromStorage(dataList.get(position).getUserId());
                        // Delete data from FireStore
                        deleteDataFromFireStore(dataList.get(position));

                        dataList.remove(position);
                        notifyItemRemoved(position);


                    }).setNegativeButton("No", (dialog, which) -> {

                    }).show();


        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(ArrayList<Data_Model> newList) {
        dataList = new ArrayList<>();
        dataList.addAll(newList);
        notifyDataSetChanged();
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

    private void setDepartSpinnerData(Spinner spinnerDepart, Context context) {

        ArrayList<String> departments = new ArrayList<>();
        departments.add("BCA");
        departments.add("BCOM");
        departments.add("BSC");

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerDepart.setAdapter(departmentAdapter);
    }

    private void setYearSpinnerData( Spinner spinnerYear, Context context) {


        ArrayList<String> years = new ArrayList<>();
        years.add("1st_Year");
        years.add("2nd_Year");
        years.add("3rd_Year");

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerYear.setAdapter(yearAdapter);
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


    private void setupStudentDialog(View myView, Data_Model dataModel) {
        EditText rollNumber = myView.findViewById(R.id.edtRoll);
        EditText enrollNumber = myView.findViewById(R.id.edtEnroll);
        EditText fatherName = myView.findViewById(R.id.edtFatherName);
        Spinner spinnerYear = myView.findViewById(R.id.Year);
        Spinner spinnerDepart = myView.findViewById(R.id.Department);
        setDepartSpinnerData(spinnerDepart, myView.getContext());
        setYearSpinnerData(spinnerYear, myView.getContext());
        Button save = myView.findViewById(R.id.btnSave);
        EditText firstName = myView.findViewById(R.id.edtFirstName);
        EditText lastName = myView.findViewById(R.id.edtLastName);
        EditText phone = myView.findViewById(R.id.edtPhone);
        EditText email = myView.findViewById(R.id.edtEmail);
        EditText password = myView.findViewById(R.id.edtPassword);

        // Get the department from dataModel
        String userDepartment = dataModel.getDepartment();
        String userYear = dataModel.getYear();

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

        firstName.setText(dataModel.getFirstName());
        lastName.setText(dataModel.getLastName());
        phone.setText(dataModel.getPhone());
        rollNumber.setText(dataModel.getRollNumber());
        enrollNumber.setText(dataModel.getEnrollmentNumber());
        fatherName.setText(dataModel.getFatherName());
        email.setText(dataModel.getEmail());
        password.setText(dataModel.getPassword());
        password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        email.setEnabled(false);
        password.setEnabled(false);

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
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("FirstName", updatedFirstName);
            updatedData.put("LastName", updatedLastName);
            updatedData.put("Phone", updatedPhone);
            updatedData.put("RollNumber", updatedRollNumber);
            updatedData.put("EnrollmentNumber", updatedEnrollNumber);
            updatedData.put("FatherName", updatedFatherName);
            updatedData.put("Department", updatedDepartment);
            updatedData.put("Year", updatedYear);

            // Get a reference to the FireStore collection and the specific document
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String collectionName = dataModel.getCollection();
            DocumentReference userRef = db.collection(collectionName).document(dataModel.getEmail());

            userRef.update(updatedData).addOnSuccessListener(unused -> {
                Toast.makeText(v1.getContext(), "Changes Updated Successfully.", Toast.LENGTH_SHORT).show();
                dialogPlus.dismiss();

            }).addOnFailureListener(e -> Toast.makeText(v1.getContext(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        dialogPlus.show();
    }

    private void setupFacultyDialog(View myView, Data_Model dataModel) {

            Button save = myView.findViewById(R.id.btnSave);
            EditText firstName = myView.findViewById(R.id.edtFirstName);
            EditText lastName = myView.findViewById(R.id.edtLastName);
            EditText phone = myView.findViewById(R.id.edtPhone);
            EditText email = myView.findViewById(R.id.edtEmail);
            EditText password = myView.findViewById(R.id.edtPassword);
            Spinner spinnerDepart = myView.findViewById(R.id.Department);
            setDepartSpinnerData(spinnerDepart,myView.getContext());

            String userDepartment = dataModel.getDepartment();

                if (userDepartment != null) {
                    int departmentIndex = getIndex(userDepartment, spinnerDepart);
                    if (departmentIndex != -1) {
                        spinnerDepart.setSelection(departmentIndex);
                    }
                }



            firstName.setText(dataModel.getFirstName());
            lastName.setText(dataModel.getLastName());
            phone.setText(dataModel.getPhone());
            email.setText(dataModel.getEmail());
            password.setText(dataModel.getPassword());
            password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            email.setEnabled(false);
            password.setEnabled(false);



        save.setOnClickListener(v1 -> {
            // Get the updated data from EditText fields
            String updatedFirstName = firstName.getText().toString();
            String updatedLastName = lastName.getText().toString();
            String updatedPhone = phone.getText().toString();
            String updatedDepartment = spinnerDepart.getSelectedItem().toString();


            // Validate input (add more validation conditions as needed)
            if (TextUtils.isEmpty(updatedFirstName) || TextUtils.isEmpty(updatedLastName) || TextUtils.isEmpty(updatedPhone)
            ) {
                // Display an error message or Toast indicating that all fields must be filled
                Toast.makeText(v1.getContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                return; // Stop further processing if validation fails
            }

                // Create a Map with the updated data
                Map<String,Object> updatedData = new HashMap<>();
                updatedData.put("FirstName", updatedFirstName);
                updatedData.put("LastName", updatedLastName);
                updatedData.put("Phone", updatedPhone);
                updatedData.put("Department",updatedDepartment);


            // Get a reference to the FireStore collection and the specific document
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String CollectionName = dataModel.getCollection();
            DocumentReference userRef = db.collection(CollectionName).document(dataModel.getEmail());

            userRef.update(updatedData).addOnSuccessListener(unused -> {
                dialogPlus.dismiss();
                Toast.makeText(v1.getContext(), "Changes Updated Successfully.", Toast.LENGTH_SHORT).show();


            }).addOnFailureListener(e -> Toast.makeText(v1.getContext(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show());

        });

        dialogPlus.show();
    }

    // Function to delete data from Firebase Storage
    private void deleteDataFromStorage(String userId) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference userStorageRef = storageReference.child("Profile_Images/" + userId);

        // Check if the file exists before attempting to delete
        userStorageRef.getMetadata().addOnSuccessListener(metadata -> {
            // File exists, proceed with deletion
            userStorageRef.delete().addOnSuccessListener(aVoid -> {
                // File deleted successfully
                // You can perform additional actions after successful deletion if needed
            }).addOnFailureListener(e -> {
                // Handle the failure to delete the file
            });
        }).addOnFailureListener(e -> {

        });
    }


    // Function to delete data from FireStore
    private void deleteDataFromFireStore(Data_Model dataModel) {
        // Get the reference to the FireStore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Specify the path to the document based on the collection and email
        DocumentReference documentReference = db.collection(dataModel.getCollection()).document(dataModel.getEmail());

        // Delete the document
        documentReference.delete().addOnSuccessListener(aVoid -> {
            // Document deleted successfully
        }).addOnFailureListener(e -> {
            // Handle the failure to delete the document
        });
    }


}
