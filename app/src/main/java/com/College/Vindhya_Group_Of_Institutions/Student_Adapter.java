package com.College.Vindhya_Group_Of_Institutions;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Student_Adapter extends RecyclerView.Adapter<Student_Adapter.MyViewHolder>{

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fNAme,lName,department , year, email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fNAme = itemView.findViewById(R.id.txtFirstName);
            lName = itemView.findViewById(R.id.txtLastName);
            department = itemView.findViewById(R.id.txtDepartment);
            year = itemView.findViewById(R.id.txtYear);
            email = itemView.findViewById(R.id.txtEmail);

        }
    }



}
