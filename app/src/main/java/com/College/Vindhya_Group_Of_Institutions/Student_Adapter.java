package com.College.Vindhya_Group_Of_Institutions;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Student_Adapter extends RecyclerView.Adapter<Student_Adapter.MyViewHolder>{

    ArrayList<Data_Model> dataList ;
    private StorageReference storageReference;

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


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fNAme,lName,department , year, email;
        ImageView profile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fNAme = itemView.findViewById(R.id.txtFirstName);
            lName = itemView.findViewById(R.id.txtLastName);
            department = itemView.findViewById(R.id.txtDepartment);
            year = itemView.findViewById(R.id.txtYear);
            profile = itemView.findViewById(R.id.imgProfile);

        }
    }

    public void loadProfileImage(ImageView profileImageView, String userId) {

        StorageReference storeRef = storageReference.child("Profile_Images/" + userId);

        storeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            profileImageView.setBackgroundColor(Color.TRANSPARENT);
            Picasso.get().load(uri).into(profileImageView);
        }).addOnFailureListener(e ->
                Toast.makeText(profileImageView.getContext(), "Failed to load Profile Image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }




}
