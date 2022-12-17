package com.example.pmsmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ReportsActivity extends AppCompatActivity {

    Button btnPropertyBalance,btnclosereports;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        btnPropertyBalance=findViewById(R.id.btnPropertyBalance);
        btnclosereports=findViewById(R.id.btnclosereports);

        btnPropertyBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intReport=new Intent(ReportsActivity.this,VillaBalanceActivity.class);
                startActivity(intReport);
            }
        });
        btnclosereports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}