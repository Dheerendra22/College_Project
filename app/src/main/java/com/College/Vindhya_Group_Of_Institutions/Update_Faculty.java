package com.College.Vindhya_Group_Of_Institutions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Update_Faculty extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Data_Model> dataList;
    FirebaseFirestore db;
    Student_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_faculty);

        recyclerView = findViewById(R.id.recViewFaculty);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        adapter = new Student_Adapter(dataList);
        recyclerView.setAdapter(adapter);

        db.collection("Faculty")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    int startPosition = dataList.size();

                    for (DocumentSnapshot d : list) {
                        Data_Model obj = d.toObject(Data_Model.class);
                        dataList.add(obj);
                    }

                    adapter.notifyItemRangeInserted(startPosition, list.size());
                });

    }
}