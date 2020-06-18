package com.mychelantonacio.packstar.view.activities;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.util.Dialogs.DiscardChangesFragmentDialog;
import com.mychelantonacio.packstar.view.fragments.ListItemFragment;
import com.mychelantonacio.packstar.view.fragments.SingleFragmentActivity;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;


public class ListItemActivity extends SingleFragmentActivity
        implements DiscardChangesFragmentDialog.NoticeDialogListener{

    private DiscardChangesFragmentDialog discardChangesFragmentDialog;
    private static final String DIALOG_DISCARD = "DiscardChangesFragmentDialog";
    private ItemViewModel itemViewModel;


    @Override
    protected Fragment createFragment() { return new ListItemFragment(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUIOnCreate();
    }

    private void setupUIOnCreate() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
    }

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

    public void showChipMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.chip_status_menu, popup.getMenu());
        popup.setGravity(5);



        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId() == R.id.menu_chip_need_to_buy){

                    Toast.makeText(ListItemActivity.this, "Need to buy", Toast.LENGTH_LONG).show();
                }
                if(item.getItemId() == R.id.menu_chip_already_have){
                    Toast.makeText(ListItemActivity.this, "Already have", Toast.LENGTH_LONG).show();
                }
                if(item.getItemId() == R.id.menu_chip_remove){
                    Toast.makeText(ListItemActivity.this, "Remove", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        popup.show();
    }
}