package com.mychelantonacio.packstar.view.activities;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.util.Dialogs.DiscardChangesFragmentDialog;
import com.mychelantonacio.packstar.util.Dialogs.HelpFragmentDialog;
import com.mychelantonacio.packstar.view.fragments.ListItemFragment;
import com.mychelantonacio.packstar.view.fragments.SingleFragmentActivity;


public class ListItemActivity extends SingleFragmentActivity
        implements DiscardChangesFragmentDialog.NoticeDialogListener,
        HelpFragmentDialog.NoticeDialogListener {

    private DiscardChangesFragmentDialog discardChangesFragmentDialog;
    private static final String DIALOG_DISCARD = "DiscardChangesFragmentDialog";
    private HelpFragmentDialog helpFragmentDialog;
    private static final String DIALOG_HELP = "HelpFragmentDialog";



    @Override
    protected Fragment createFragment() { return new ListItemFragment(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUIOnCreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    private void setupUIOnCreate() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            discardChangesFragmentDialog = new DiscardChangesFragmentDialog();
            discardChangesFragmentDialog.show(fragmentManager, DIALOG_DISCARD);
        }
        else if (item.getItemId() == R.id.ic_help) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            helpFragmentDialog = new HelpFragmentDialog();
            helpFragmentDialog.show(fragmentManager, DIALOG_HELP);
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

    @Override
    public void onDialogOkClick(DialogFragment dialog) {}
}