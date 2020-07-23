package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mychelantonacio.packstar.R;

public class EmptyBagActivity extends AppCompatActivity {

    private FloatingActionButton fab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_bag);
        setupUIOnCreate();
    }

    private void setupUIOnCreate(){
        fab = (FloatingActionButton) findViewById(R.id.fab_plus);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(EmptyBagActivity.this, CreateBagActivity.class);
            startActivity(intent);
        });
    }
}