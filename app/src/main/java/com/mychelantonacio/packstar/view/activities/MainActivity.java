package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.view.adapters.BagListAdapter;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;

import java.util.List;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class MainActivity extends AppCompatActivity {

    private BagViewModel bagViewModel;
    private BagListAdapter bagAdapter;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);
        initialSetup();

        if(isFirstRun()) {
            setupFirstRun();
        }
        else if(bagAdapter.getItemCount() > 0){
            Intent intent = new Intent(MainActivity.this, EmptyBagActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(MainActivity.this, ListBagActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initialSetup(){
        //debugging purpose...
        Stetho.initializeWithDefaults(this);
        bagAdapter = new BagListAdapter(this);
        bagViewModel = new ViewModelProvider(this).get(BagViewModel.class);

        bagViewModel.getAllBagsSortedByName().observe(this, new Observer<List<Bag>>() {
            @Override
            public void onChanged(List<Bag> bags) {
                bagAdapter.setBags(bags);
            }
        });
    }

    private void setupFirstRun() {
        setContentView(R.layout.activity_main);
        FloatingActionButton fab;
        PulsatorLayout pulsator;

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(getResources().getString((R.string.shared_preferences_first_run)), Boolean.FALSE);
        edit.commit();

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
    }

    private boolean isFirstRun(){
        boolean isFirstRun;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        isFirstRun = sharedPreferences.getBoolean(getResources().getString((R.string.shared_preferences_first_run)), true);
        return isFirstRun;
    }
}