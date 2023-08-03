package com.alpha.silentme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DashboardActivity extends AppCompatActivity {


    //Test
    Button btnSetLocation;
    Button btnHandyCalulator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initVar();

        setMethods();
    }

    private void setMethods() {
        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,MainActivity.class));
            }
        });
    }

    private void initVar() {
        btnSetLocation= findViewById(R.id.btnSetLocation);
        btnHandyCalulator= findViewById(R.id.btnHandyCalculator);
    }
}