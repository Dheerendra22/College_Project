package com.College.Vindhya_Group_Of_Institutions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Allotment_Adapter extends RecyclerView.Adapter<Allotment_Adapter.MyViewHolder> {

    // ArrayList to hold the data for the RecyclerView
    private ArrayList<Data_Model> dataList;

    // Firebase Storage reference for loading profile images
    private final StorageReference storageReference;

    // OnClickListener for allot ImageView
    private final View.OnClickListener allotClickListener;

    // Constructor to initialize the adapter
    public Allotment_Adapter(ArrayList<Data_Model> dataList) {
        this.dataList = dataList;

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // OnClickListener for allot ImageView
        allotClickListener = v -> {
            int position = (int) v.getTag(); // Get the clicked position
            openNewActivity(dataList.get(position), v.getContext());
        };
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each row
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allot_subject_row, parent, false);
        return new MyViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Set data to the views in each row
        holder.fNAme.setText(dataList.get(position).getFirstName());
        holder.lName.setText(dataList.get(position).getLastName());
        holder.department.setText(dataList.get(position).getDepartment());
        loadProfileImage(holder.profile, dataList.get(position).getUserId());

        // Set the OnClickListener and tag the position
        holder.allot.setOnClickListener(allotClickListener);
        holder.allot.setTag(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    // Load profile image from Firebase Storage using Picasso library
    public void loadProfileImage(ImageView profileImageView, String userId) {
        StorageReference storeRef = storageReference.child("Profile_Images/" + userId);

        storeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            profileImageView.setBackgroundColor(Color.TRANSPARENT);
            Picasso.get().load(uri).into(profileImageView);
        }).addOnFailureListener(e -> {
            // Handle failure to load image
        });
    }

    // ViewHolder class to hold the views for each row
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView fNAme, lName, department;
        ImageView profile, allot;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views in the row layout
            fNAme = itemView.findViewById(R.id.txtFirstName);
            lName = itemView.findViewById(R.id.txtLastName);
            department = itemView.findViewById(R.id.txtDepartment);
            profile = itemView.findViewById(R.id.imgProfile);
            allot = itemView.findViewById(R.id.imgAllot);
        }
    }

    // Set a new filtered dataset to the adapter
    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(ArrayList<Data_Model> newList) {
        dataList = new ArrayList<>();
        dataList.addAll(newList);
        notifyDataSetChanged();
    }

    // Open a new activity with details from the clicked row
    private void openNewActivity(Data_Model dataModel, Context context) {
        // Assuming you have a context available, replace "YourNewActivity.class" with the actual class of your new activity
        Intent intent = new Intent(context, Subject_List.class);

        // Pass any necessary data to the new activity using Intent extras
        intent.putExtra("firstName", dataModel.getFirstName());
        intent.putExtra("lastName", dataModel.getLastName());
        intent.putExtra("department", dataModel.getDepartment());
        intent.putExtra("email", dataModel.getEmail());
        intent.putExtra("SubjectList", dataModel.getSubjectList());

        // Start the new activity
        context.startActivity(intent);
    }
}
