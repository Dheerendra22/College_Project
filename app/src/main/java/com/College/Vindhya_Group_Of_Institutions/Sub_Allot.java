package com.College.Vindhya_Group_Of_Institutions;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Sub_Allot extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<Data_Model> dataList;
    FirebaseFirestore db;
    Allotment_Adapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_allot);

        recyclerView = findViewById(R.id.recViewAllot);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        adapter = new Allotment_Adapter(dataList);
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
                    if (dataList.isEmpty()) {
                        // Show a Toast message indicating that no data was retrieved
                        Toast.makeText(Sub_Allot.this, "No data found", Toast.LENGTH_LONG).show();
                    } else {
                        // Data is available, update the adapter
                        adapter.notifyItemRangeInserted(startPosition, list.size());

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_recview,menu);
        MenuItem item = menu.findItem(R.id.search);

        SearchView sv = (SearchView) item.getActionView();

        assert sv != null;
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                process_search(query);
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                process_search(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    private void process_search(String query){

        ArrayList<Data_Model> filteredList = new ArrayList<>();

        for (Data_Model data : dataList) {
            if (data.getFirstName().toLowerCase().contains(query.toLowerCase()) ||
                    data.getLastName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(data);
            }
        }

        // Update the adapter with the filtered list
        adapter.setFilter(filteredList);
    }
}