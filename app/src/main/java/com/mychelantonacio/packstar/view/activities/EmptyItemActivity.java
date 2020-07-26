package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.util.Dialogs.DiscardChangesFragmentDialog;

public class EmptyItemActivity extends AppCompatActivity
        implements DiscardChangesFragmentDialog.NoticeDialogListener {

    private FloatingActionButton fab;
    private Bag currentBag;
    private DiscardChangesFragmentDialog discardChangesFragmentDialog;
    private static final String DIALOG_DISCARD = "DiscardChangesFragmentDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_item);
        setupUIOnCreate();
    }

    private void setupUIOnCreate(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent intentParcelable = getIntent();
        currentBag = (Bag) intentParcelable.getParcelableExtra("selected_bag");
        fab = (FloatingActionButton) findViewById(R.id.fab_plus);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateItemActivity.class);
            intent.putExtra("bag_parcelable", currentBag);
            startActivity(intent);
        });
    }

    //back button
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            discardChangesFragmentDialog = new DiscardChangesFragmentDialog();
            discardChangesFragmentDialog.show(fragmentManager, DIALOG_DISCARD);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //Discard button...
        this.finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Cancel button...
        dialog.dismiss();
    }
}