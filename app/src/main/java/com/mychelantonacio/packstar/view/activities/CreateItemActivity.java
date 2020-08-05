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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.Dialogs.DiscardChangesFragmentDialog;
import com.mychelantonacio.packstar.util.Dialogs.OverSystemWeightFragmentDialog;
import com.mychelantonacio.packstar.util.enums.ItemStatusEnum;
import com.mychelantonacio.packstar.util.filters.DecimalDigitsInputFilter;
import com.mychelantonacio.packstar.view.adapters.BagListAdapter;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;


public class CreateItemActivity extends AppCompatActivity
        implements DiscardChangesFragmentDialog.NoticeDialogListener,
        OverSystemWeightFragmentDialog.NoticeDialogListener {


    private ItemStatusEnum itemStatus;
    private double MAX_SYSTEM_WEIGHT = 100;


    //dialogs
    private DiscardChangesFragmentDialog discardChangesFragmentDialog;
    private static final String DIALOG_DISCARD = "DiscardChangesFragmentDialog";
    private OverSystemWeightFragmentDialog overSystemWeightFragmentDialog;
    private static final String DIALOG_OVER_WEIGHT = "OverSystemWeightFragmentDialog";

    //Widgets
    private TextInputEditText nameEditText;
    private TextInputEditText quantityEditText;
    private TextInputEditText weightEditText;
    private ExtendedFloatingActionButton eFab;
    private Chip chipAlreadyHave;
    private Chip chipNeedToBuy;

    //data
    private Bag currentBag;
    private BagListAdapter bagAdapter;
    private BagViewModel bagViewModel;
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
        chipNeedToBuy = (Chip) findViewById(R.id.chip_need_to_buy);
        chipNeedToBuySetup();
        chipAlreadyHave = (Chip) findViewById(R.id.chip_already_have);
        chipAlreadyHaveSetup();
        eFab = (ExtendedFloatingActionButton) findViewById(R.id.floatingActionButton);
        fabSetup();
        itemStatus = ItemStatusEnum.NON_INFORMATION;
        Intent intent = getIntent();
        currentBag = (Bag) intent.getParcelableExtra("bag_parcelable");
        bagAdapter = new BagListAdapter(this);

        bagViewModel = new ViewModelProvider(this).get(BagViewModel.class);
        bagViewModel.getAllBagsSortedByName().observe(this, bags -> bagAdapter.setBags(bags));
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.getAllItems().observe(this, items -> bagAdapter.setItems(items));
    }

    private void chipNeedToBuySetup(){
        chipNeedToBuy.setOnClickListener(v -> {
            if(!chipNeedToBuy.isChecked()){
                chipNeedToBuy.setChecked(false);
                itemStatus = ItemStatusEnum.NON_INFORMATION;
            }
            else{
                chipNeedToBuy.setChecked(true);
                chipAlreadyHave.setChecked(false);
                itemStatus = ItemStatusEnum.NEED_TO_BUY;
            }
        });
    }

    private void chipAlreadyHaveSetup(){
        chipAlreadyHave.setOnClickListener(v -> {
            if(!chipAlreadyHave.isChecked() ){
                chipAlreadyHave.setChecked(false);
                itemStatus = ItemStatusEnum.NON_INFORMATION;
            }
            else{
                chipAlreadyHave.setChecked(true);
                chipNeedToBuy.setChecked(false);
                itemStatus = ItemStatusEnum.ALREADY_HAVE;
            }
        });
    }

    private void fabSetup(){
        eFab.setOnClickListener(v -> createItem());
    }

    private void createItem(){
        if (isNameEmpty() || isQuantityEmpty()) { return; }
        Item newItem = new Item();
        newItem.setName(nameEditText.getText().toString());
        newItem.setQuantity(Integer.valueOf(quantityEditText.getText().toString()));
        if(!TextUtils.isEmpty(weightEditText.getText().toString())){
            newItem.setWeight(new Double(weightEditText.getText().toString()));
            if(isBagOverSystemWeight(newItem)){
                FragmentManager fragmentManager = getSupportFragmentManager();
                overSystemWeightFragmentDialog = new OverSystemWeightFragmentDialog();
                overSystemWeightFragmentDialog.show(fragmentManager, DIALOG_OVER_WEIGHT);
                return;
            }

        }
        newItem.setStatus(itemStatus.getStatusCode());
        newItem.setBagId(currentBag.getId());
        itemViewModel.insert(newItem);
        callIntent();
    }

    private boolean isBagOverSystemWeight(Item newItem){
        double totalCurrentBagWeight = bagAdapter.getItemWeight(currentBag);
        totalCurrentBagWeight += (newItem.getWeight() * newItem.getQuantity());
        if(totalCurrentBagWeight >= MAX_SYSTEM_WEIGHT){
            return true;
        }
        else {
            return false;
        }
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
            nameEditText.setError(getResources().getString(R.string.alert_create_item_name_required));
            return true;
        }
        return false;
    }

    private boolean isQuantityEmpty(){
        String quantityName = quantityEditText.getText().toString().trim();
        if(TextUtils.isEmpty(quantityName)){
            quantityEditText.setError(getResources().getString(R.string.alert_create_item_quantity_required));
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        this.finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    private boolean isAnyFieldFilled(){
        if (!nameEditText.getText().toString().isEmpty() || !quantityEditText.getText().toString().isEmpty() ||
                !weightEditText.getText().toString().isEmpty() ){
            return true;
        }
        return false;
    }

    @Override
    public void onDialogOverWeightPositiveClick(DialogFragment dialog) {
        dialog.dismiss();
    }
}