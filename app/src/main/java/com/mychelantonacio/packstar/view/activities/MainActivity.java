package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.repository.BagRepository;

import java.util.concurrent.atomic.AtomicInteger;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private BagRepository bagRepository;
    private int bagCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_App);
        super.onCreate(savedInstanceState);

        try {
            initialSetup();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(isFirstRun()) {
            setupFirstRun();
        }
        else if(bagCount > 0){
            Intent intent = new Intent(MainActivity.this, ListBagActivity.class);
            startActivity(intent);
            finish();
        }
        else if(bagCount == 0){
            Intent intent = new Intent(MainActivity.this, EmptyBagActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initialSetup() throws InterruptedException {
        bagRepository = new BagRepository(getApplication());
        //startStetho();//TODO: cut it off before deploy to production
        bagCount = getCountBags();
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

    private void startStetho(){
        //debugging purpose...
        Stetho.initializeWithDefaults(this);
    }

    private int getCountBags() throws InterruptedException {
        final AtomicInteger fcount = new AtomicInteger();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int num = bagRepository.getCount();
                fcount.set(num);
            }
        });
        t.setPriority(10);
        t.start();
        t.join();
        return fcount.intValue();
    }
}