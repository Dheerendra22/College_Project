package com.example.experiment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = findViewById(R.id.button_login);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity2();

            }
        });
    }

    public void Activity2(){
        Intent intent = new Intent(this, NewActivity.class );
        startActivity(intent);

    }



}