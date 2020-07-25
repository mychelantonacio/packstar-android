package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;

public class EmptyItemActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private Bag currentBag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_item);
        setupUIOnCreate();
    }

    private void setupUIOnCreate(){
        Intent intentParcelable = getIntent();
        currentBag = (Bag) intentParcelable.getParcelableExtra("selected_bag");
        fab = (FloatingActionButton) findViewById(R.id.fab_plus);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateItemActivity.class);
            intent.putExtra("bag_parcelable", currentBag);
            startActivity(intent);
        });
    }
}