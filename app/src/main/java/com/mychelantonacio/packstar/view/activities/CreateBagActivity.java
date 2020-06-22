package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.Dialogs.DatePickerFragmentDialog;
import com.mychelantonacio.packstar.util.Dialogs.DiscardChangesFragmentDialog;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;
import java.text.ParseException;
import java.util.Calendar;


public class CreateBagActivity extends AppCompatActivity
        implements DiscardChangesFragmentDialog.NoticeDialogListener,
        DatePickerFragmentDialog.DatePickerFragmentListener{


    private DiscardChangesFragmentDialog discardChangesFragmentDialog;
    private static final String DIALOG_DISCARD = "DiscardChangesFragmentDialog";
    private DatePickerFragmentDialog datePickerFragmentDialog;
    private static final String DIALOG_DATE_PICKER = "DatePickerFragmentDialog";

    private String DATE_DIALOG;
    private static final String EDIT_TEXT_DATE_DIALOG = "editTextDateDialog";
    private static final String REMINDER_DATE_TIME_DIALOG = "reminderDateTimeDialog";

    //Widgets
    private TextInputEditText nameEditText;
    private TextInputEditText dateEditText;
    private TextInputEditText weightEditText;
    private TextInputEditText commentEditText;
    private ImageButton reminderButton;
    private ExtendedFloatingActionButton eFab;

    //Data
    private BagViewModel bagViewModel;
    private ItemViewModel itemViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bag);
        setupUIOnCreate();
    }

    private void setupUIOnCreate() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        nameEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_name);
        dateEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_date);
        weightEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_weight);
        commentEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_comment);
        dateEditTextSetup();
        reminderButton = (ImageButton) findViewById(R.id.ic_reminder);
        eFab = (ExtendedFloatingActionButton) findViewById(R.id.floatingActionButton);
        fabSetup();
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        bagViewModel = new ViewModelProvider(this).get(BagViewModel.class);
    }

    //it avoids conflict with datepicker action...
    private void dateEditTextSetup(){
        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dateEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                nameEditText.setFocusable(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void fabSetup(){
        eFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBag();
            }
        });
    }
    private void createBag(){
/*
        if (isNameEmpty() || isDateEmpty()) {
            return;
        }
        Bag newBag = new Bag();
        newBag.setName(nameEditText.getText().toString());
        newBag.setTravelDate(dateEditText.getText().toString());
        if(!TextUtils.isEmpty(weightEditText.getText().toString())){
            newBag.setWeight(new Double(weightEditText.getText().toString()));
        }
        newBag.setComment(commentEditText.getText().toString());
        bagViewModel.insert(newBag);

        //TODO: correct clean fields regards activity lifecycle...
        nameEditText.setText("");
        dateEditText.setText("");
        weightEditText.setText("");
        commentEditText.setText("");
*/


        //prePopulateForTestingPurpose();
        Intent intent = new Intent(CreateBagActivity.this, ListBagActivity.class);
        startActivity(intent);
    }

    private void prePopulateForTestingPurpose(){
        //bagViewModel.deleteAll();
        for(int i = 1; i <= 100; i++){
           // Bag bag = new Bag("Test Bag " + i, "01/01/2020", new Double(i), "Test Comment " + i);
            //bagViewModel.insert(bag);

            for(int j = 1; j <= 3; j++){
                Item item = new Item();

                item.setBagId(new Long(i) );
                item.setName("Item " + j);
                item.setQuantity(j);
                item.setWeight(new Double(j));

                if(j % 2 == 0)
                    item.setStatus("B");
                else
                    item.setStatus("A");

                itemViewModel.insert(item);
            }

        }
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

    //Date and TimeDate dialogs
    @Override
    public void onDateSet(int year, int month, int day) {
        if(DATE_DIALOG == EDIT_TEXT_DATE_DIALOG){
            dateEditText.setText(day + "/" + (++month) + "/" + year);
        }
        else if(DATE_DIALOG == REMINDER_DATE_TIME_DIALOG){
            showTimePickerDialog(year, month, day);
        }
    }

    public void showDatePickerDialog(View v) {
        DATE_DIALOG = EDIT_TEXT_DATE_DIALOG;
        openDialog();
    }

    public void openDialog(){
        DatePickerFragmentDialog datePickerFragmentDialog = new DatePickerFragmentDialog();
        datePickerFragmentDialog.show(getSupportFragmentManager(), DIALOG_DATE_PICKER);
    }

    public void showDatePickerReminderDialog(View v) {
        DATE_DIALOG = REMINDER_DATE_TIME_DIALOG;
        openDialog();
    }

    private void showTimePickerDialog(final int year, final int month, final int day){
        final Calendar c = Calendar.getInstance();
        final int currentHour = c.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = c.get(Calendar.MINUTE);
        final int monthPlusOne = month + 1;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                try {
                    if(isUserTimeAfterCurrentTime(hourOfDay, minute)){
                        Toast.makeText(CreateBagActivity.this,
                                "Alarm set to " + day + "/" + (monthPlusOne) + "/" + year + " at " + hourOfDay + "h : " + minute + "m",
                                Toast.LENGTH_LONG).show();

                        //TODO: call reminder function passing this date/time here...
                    }
                    else{
                        Toast.makeText(CreateBagActivity.this,
                                "Invalid time. Please, set future reminder time.", Toast.LENGTH_LONG).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, currentHour, currentMinute, true);
        timePickerDialog.show();
    }

    private boolean isUserTimeAfterCurrentTime(int hourOfDay, int minute) throws ParseException {
        Calendar c = Calendar.getInstance();
        if ( hourOfDay >= c.get(Calendar.HOUR_OF_DAY) && minute > c.get(Calendar.MINUTE) ) {
            Log.i("hourOfDay", hourOfDay + " >= " + c.get(Calendar.HOUR_OF_DAY) + " && " + minute + " > " + c.get(Calendar.MINUTE));
            return true;
        }
        else{
            return false;
        }
    }

    private boolean isNameEmpty(){
        String bagName = nameEditText.getText().toString().trim();
        if(TextUtils.isEmpty(bagName)){
            nameEditText.setError("Please, enter Bag name");
            return true;
        }
        return false;
    }

    private boolean isDateEmpty(){
        String bagDate = dateEditText.getText().toString();
        if(TextUtils.isEmpty(bagDate)){
            dateEditText.setError("Please, enter Date name");
            return true;
        }
        return false;
    }
}//endClass...