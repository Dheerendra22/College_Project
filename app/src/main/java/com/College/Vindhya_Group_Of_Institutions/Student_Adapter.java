package com.College.Vindhya_Group_Of_Institutions;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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

            setSpinnerData(spinnerDepart,spinnerYear,holder.profile.getContext());

            firstName.setText(dataList.get(position).getFirstName());
            lastName.setText(dataList.get(position).getLastName());
            phone.setText(dataList.get(position).getPhone());
            rollNumber.setText(dataList.get(position).getRollNumber());
            enrollNumber.setText(dataList.get(position).getEnrollmentNumber());
            fatherName.setText(dataList.get(position).getFatherName());

            dialogPlus.show();



        });




    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fNAme,lName,department , year;
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



}
