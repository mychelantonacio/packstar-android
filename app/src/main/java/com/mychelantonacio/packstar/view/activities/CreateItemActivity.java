package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.MenuItem;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.Dialogs.DiscardChangesFragmentDialog;
import com.mychelantonacio.packstar.util.enums.ItemStatusEnum;
import com.mychelantonacio.packstar.util.filters.DecimalDigitsInputFilter;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;


public class CreateItemActivity extends AppCompatActivity
        implements DiscardChangesFragmentDialog.NoticeDialogListener {

    private ItemStatusEnum itemStatus;
    private DiscardChangesFragmentDialog discardChangesFragmentDialog;
    private static final String DIALOG_DISCARD = "DiscardChangesFragmentDialog";

    //Widgets
    private TextInputEditText nameEditText;
    private TextInputEditText quantityEditText;
    private TextInputEditText weightEditText;
    private ExtendedFloatingActionButton eFab;
    private ChipGroup chipGroup;

    //DATA
    private Bag currentBag;
    private ItemViewModel itemViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);
        setupUIOnCreate();
    }

    private void setupUIOnCreate() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        nameEditText = (TextInputEditText) findViewById(R.id.editText_item_name);
        quantityEditText = (TextInputEditText) findViewById(R.id.editText_item_quantity);
        weightEditText = (TextInputEditText) findViewById(R.id.editText_item_weight);
        weightEditText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2,3)});
        eFab = (ExtendedFloatingActionButton) findViewById(R.id.floatingActionButton);
        fabSetup();
        chipGroup = (ChipGroup) findViewById(R.id.chip_group);
        chipGroupSetup();
        itemStatus = ItemStatusEnum.NON_INFORMATION;
        Intent intent = getIntent();
        currentBag = (Bag) intent.getParcelableExtra("bag_parcelable");
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
    }


    private void fabSetup(){
        eFab.setOnClickListener(v -> createItem());
    }

    private void chipGroupSetup(){
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String needToBuy = "Need to buy";
            String alreadyHave = "Already have";
            Chip chip = chipGroup.findViewById(checkedId);
            if(chip == null) {
                itemStatus = ItemStatusEnum.NON_INFORMATION;
            }
            else if(chip.getText().toString().equals(needToBuy)){
                itemStatus = ItemStatusEnum.NEED_TO_BUY;
            }
            else{
                itemStatus = ItemStatusEnum.ALREADY_HAVE;
            }
        });
    }

    private void createItem(){
        if (isNameEmpty() || isQuantityEmpty()) { return; }
        Item newItem = new Item();
        newItem.setName(nameEditText.getText().toString());
        newItem.setQuantity(Integer.valueOf(quantityEditText.getText().toString()));
        if(!TextUtils.isEmpty(weightEditText.getText().toString())){
            newItem.setWeight(new Double(weightEditText.getText().toString()));
        }
        newItem.setStatus(itemStatus.getStatusCode());
        newItem.setBagId(currentBag.getId());
        itemViewModel.insert(newItem);
        callIntent();
    }

    private void callIntent(){
        Intent intent = new Intent(CreateItemActivity.this, ListItemActivity.class);
        intent.putExtra("selected_bag", currentBag);
        startActivity(intent);
        finish();
    }

    private boolean isNameEmpty(){
        String itemName = nameEditText.getText().toString().trim();
        if(TextUtils.isEmpty(itemName)){
            nameEditText.setError("Please, enter Item name");
            return true;
        }
        return false;
    }

    private boolean isQuantityEmpty(){
        String quantityName = quantityEditText.getText().toString().trim();
        if(TextUtils.isEmpty(quantityName)){
            quantityEditText.setError("Please, enter Item quantity");
            return true;
        }
        return false;
    }

    //back button
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(isAnyFieldFilled()) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                discardChangesFragmentDialog = new DiscardChangesFragmentDialog();
                discardChangesFragmentDialog.show(fragmentManager, DIALOG_DISCARD);
            }
            else{
                this.finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isAnyFieldFilled(){
        if (!nameEditText.getText().toString().isEmpty() || !quantityEditText.getText().toString().isEmpty() ||
                !weightEditText.getText().toString().isEmpty() ){
            return true;
        }
        return false;
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