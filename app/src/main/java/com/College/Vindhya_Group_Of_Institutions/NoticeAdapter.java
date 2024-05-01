package com.College.Vindhya_Group_Of_Institutions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewAdapter> {

    private ArrayList<NoticeData> list;
    private Context context;

    public NoticeAdapter(ArrayList<NoticeData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public NoticeViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newsfeed_item_layout, parent, false);
        return new NoticeViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewAdapter holder, @SuppressLint("RecyclerView") int position) {

        NoticeData currentItem = list.get(position);
        holder.deleteNoticeTitle.setText(currentItem.getTitle());


        try {
            if (currentItem.getImage() != null){
                Picasso.get().load(currentItem.getImage()).into(holder.deleteNoticeImage);}
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        holder.deleteNotice.setOnClickListener(v -> {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notice");
            reference.child(currentItem.getKey()).removeValue()
                    .addOnCompleteListener(task -> Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_SHORT).show());
            notifyItemRemoved(position);
        });

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class NoticeViewAdapter extends RecyclerView.ViewHolder {

        private Button deleteNotice;
        private TextView deleteNoticeTitle;
        private ImageView deleteNoticeImage;


        public NoticeViewAdapter(@NonNull View itemView) {
            super(itemView);
            deleteNotice = itemView.findViewById(R.id.deleteNotice);
            deleteNoticeTitle = itemView.findViewById(R.id.deleteNoticeTitle);
            deleteNoticeImage = itemView.findViewById(R.id.deleteNoticeImage);
        }
    }
}
