package com.example.bengkelgis_client;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        // intent for maps
        Button mapsBtn = (Button) findViewById(R.id.btnmaps);
        Button listBtn  = (Button) findViewById(R.id.btnlist);
        Button formbtn  = (Button) findViewById(R.id.btnadd);
        mapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, MapsActivity.class);
                startActivity(i);
            }
        });
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(Dashboard.this, ListBengkel.class);
                startActivity(s);
            }
        });

        formbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent l = new Intent(Dashboard.this, FormBengkel.class);
                startActivity(l);
            }
        });
    }
}
