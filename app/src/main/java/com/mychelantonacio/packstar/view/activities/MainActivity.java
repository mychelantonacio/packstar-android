package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mychelantonacio.packstar.R;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUIOnCreate();
    }

    private void setupUIOnCreate() {
        FloatingActionButton fab;
        PulsatorLayout pulsator;

        //no title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        fab = (FloatingActionButton) findViewById(R.id.fab_plus);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateBagActivity.class);
            startActivity(intent);
        });
        pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();
        //debugging purpose...
        Stetho.initializeWithDefaults(this);
    }
}