package com.example.experiment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class NewActivity extends AppCompatActivity {

    Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_register);


        button2 = findViewById(R.id.buttonSignUp);


        Spinner spinner = findViewById(R.id.Department);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(NewActivity.this, "Selected Department: "+ item, Toast.LENGTH_SHORT).show();}

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }});

             ArrayList<String> arrayList = new ArrayList<>();
             arrayList.add("BCA"); arrayList.add("BCom"); arrayList.add("BSC");


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner.setAdapter(adapter);




        Spinner spinner2 = findViewById(R.id.Year);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item2 = parent.getItemAtPosition(position).toString();
                Toast.makeText(NewActivity.this, "Selected Year: "+ item2, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });


        ArrayList<String> arrayList2 = new ArrayList<>();
        arrayList2.add("1st_Year");
        arrayList2.add("2nd_Year");
        arrayList2.add("3rd_Year");


        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayList2);
        adapter2.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner2.setAdapter(adapter2);



    }
}