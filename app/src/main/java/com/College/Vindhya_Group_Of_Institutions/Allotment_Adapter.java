package com.College.Vindhya_Group_Of_Institutions;


import android.annotation.SuppressLint;
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

    ArrayList<Data_Model> dataList ;
    private final StorageReference storageReference;


    public Allotment_Adapter(ArrayList<Data_Model> dataList) {
        this.dataList = dataList;
        storageReference = FirebaseStorage.getInstance().getReference();

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allot_subject_row, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.fNAme.setText(dataList.get(position).getFirstName());
        holder.lName.setText(dataList.get(position).getLastName());
        holder.department.setText(dataList.get(position).getDepartment());
        loadProfileImage(holder.profile,dataList.get(position).getUserId());
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void loadProfileImage(ImageView profileImageView, String userid) {
        StorageReference storeRef = storageReference.child("Profile_Images/" + userid);

        storeRef.getDownloadUrl().addOnSuccessListener(uri -> {
            profileImageView.setBackgroundColor(Color.TRANSPARENT);
            Picasso.get().load(uri).into(profileImageView);
        }).addOnFailureListener(e -> {

        });
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fNAme,lName,department ;
        ImageView profile,allot;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fNAme = itemView.findViewById(R.id.txtFirstName);
            lName = itemView.findViewById(R.id.txtLastName);
            department = itemView.findViewById(R.id.txtDepartment);
            profile = itemView.findViewById(R.id.imgProfile);
            allot = itemView.findViewById(R.id.imgAllot);


        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilter(ArrayList<Data_Model> newList) {
        dataList = new ArrayList<>();
        dataList.addAll(newList);
        notifyDataSetChanged();
    }
}
