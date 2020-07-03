package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.Dialogs.DiscardChangesFragmentDialog;
import com.mychelantonacio.packstar.util.enums.ItemStatusEnum;
import com.mychelantonacio.packstar.view.adapters.BagListAdapter;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;

import java.util.List;


public class EditItemActivity extends AppCompatActivity
        implements DiscardChangesFragmentDialog.NoticeDialogListener {

    //Dialogs
    private DiscardChangesFragmentDialog discardChangesFragmentDialog;
    private static final String DIALOG_DISCARD = "DiscardChangesFragmentDialog";

    //Widgets
    private TextInputEditText nameEditText;
    private com.google.android.material.textfield.TextInputLayout nameTextInputLayout;
    private TextInputEditText quantityEditText;
    private com.google.android.material.textfield.TextInputLayout quantityTextInputLayout;
    private TextInputEditText weightEditText;
    private com.google.android.material.textfield.TextInputLayout weightTextInputLayout;
    private com.google.android.material.chip.ChipGroup statusChipGroup;
    private com.google.android.material.chip.Chip statusChipNeedToBuy;
    private com.google.android.material.chip.Chip statusChipAlreadyHave;
    private ExtendedFloatingActionButton eFab;

    //DATA
    private ItemViewModel itemViewModel;
    private BagViewModel bagViewModel;
    private BagListAdapter bagAdapter;
    private ItemStatusEnum itemStatus;
    private Item currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        setupUIOnCreate();
    }

    private void setupUIOnCreate() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        nameEditText = (TextInputEditText) findViewById(R.id.editText_item_name);
        nameTextInputLayout = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.filledTextField_item_name);
        quantityEditText = (TextInputEditText) findViewById(R.id.editText_item_quantity);
        quantityTextInputLayout = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.filledTextField_item_quantity);
        weightEditText = (TextInputEditText) findViewById(R.id.editText_item_weight);
        weightTextInputLayout = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.filledTextField_item_weight);
        statusChipGroup = (com.google.android.material.chip.ChipGroup)  findViewById(R.id.chip_group_edit);

        chipGroupSetup();
        statusChipNeedToBuy = (com.google.android.material.chip.Chip) findViewById(R.id.chip_red_edit);
        statusChipAlreadyHave = (com.google.android.material.chip.Chip) findViewById(R.id.chip_green_edit);
        eFab = (ExtendedFloatingActionButton) findViewById(R.id.floatingActionButton);
        fabSetup();

        Intent intent = getIntent();
        currentItem = (Item) intent.getParcelableExtra("item_parcelable");

        nameEditText.setText(currentItem.getName());
        nameTextInputLayout.setEndIconVisible(false);
        quantityEditText.setText(String.valueOf(currentItem.getQuantity()));
        quantityTextInputLayout.setEndIconVisible(false);
        weightEditText.setText(String.valueOf(currentItem.getWeight()));
        weightTextInputLayout.setEndIconVisible(false);

        if (currentItem.getStatus().equals(ItemStatusEnum.NEED_TO_BUY.getStatusCode()) ) {
            statusChipNeedToBuy.setChecked(true);
            statusChipAlreadyHave.setChecked(false);
            itemStatus = ItemStatusEnum.NEED_TO_BUY;
        }
        else if(currentItem.getStatus().equals(ItemStatusEnum.ALREADY_HAVE.getStatusCode())){
            statusChipAlreadyHave.setChecked(true);
            statusChipNeedToBuy.setChecked(false);
            itemStatus = ItemStatusEnum.ALREADY_HAVE;
        }
        else {
            itemStatus = ItemStatusEnum.NON_INFORMATION;
        }

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        bagAdapter = new BagListAdapter(this);
        bagViewModel = new ViewModelProvider(this).get(BagViewModel.class);
        bagViewModel.getAllBagsSortedByName().observe(this, new Observer<List<Bag>>() {
            @Override
            public void onChanged(List<Bag> bags) {
                bagAdapter.setBags(bags);
            }
        });
    }

    private void fabSetup(){
        eFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItem();
            }
        });
    }

    private void editItem(){
        if (isNameEmpty() || isQuantityEmpty()) { return; }
        currentItem.setName(nameEditText.getText().toString());
        currentItem.setQuantity(Integer.valueOf(quantityEditText.getText().toString()));
        if(!TextUtils.isEmpty(weightEditText.getText().toString())){
            currentItem.setWeight(new Double(weightEditText.getText().toString()));
        }
        currentItem.setStatus(itemStatus.getStatusCode());
        itemViewModel.update(currentItem);
        Bag currentBag = bagAdapter.findBagById(currentItem.getBagId());
        if (currentBag != null){
            Intent intent = new Intent(this, ListItemActivity.class);
            intent.putExtra("selected_bag", currentBag);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(this, ListBagActivity.class);
            startActivity(intent);
        }
    }

    private void chipGroupSetup(){
        statusChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                String needToBuy = "Need to buy";
                String alreadyHave = "Already have";
                Chip chip = statusChipGroup.findViewById(checkedId);
                if(chip == null) {
                    itemStatus = ItemStatusEnum.NON_INFORMATION;
                }
                else if(chip.getText().toString().equals(needToBuy)){
                    itemStatus = ItemStatusEnum.NEED_TO_BUY;
                }
                else{
                    itemStatus = ItemStatusEnum.ALREADY_HAVE;
                }
            }
        });
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