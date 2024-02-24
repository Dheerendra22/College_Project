package com.College.Vindhya_Group_Of_Institutions;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Update_Student extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Data_Model> dataList;
    FirebaseFirestore db;
    Student_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_student);


        recyclerView = findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        adapter = new Student_Adapter(dataList);
        recyclerView.setAdapter(adapter);

        // Retrieve values from the intent
        String departmentValue = getIntent().getStringExtra("Department");
        String yearValue = getIntent().getStringExtra("Year");


        db.collection("Students")
                .whereEqualTo("Department",departmentValue )
                .whereEqualTo("Year", yearValue)
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