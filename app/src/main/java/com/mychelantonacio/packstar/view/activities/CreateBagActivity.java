package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;


public class CreateBagActivity extends AppCompatActivity
        implements DiscardChangesFragmentDialog.NoticeDialogListener,
        DatePickerFragmentDialog.DatePickerFragmentListener {


    private DiscardChangesFragmentDialog discardChangesFragmentDialog;
    private static final String DIALOG_DISCARD = "DiscardChangesFragmentDialog";
    private DatePickerFragmentDialog datePickerFragmentDialog;
    private static final String DIALOG_DATE_PICKER = "DatePickerFragmentDialog";

    private String DATE_DIALOG;
    private static final String EDIT_TEXT_DATE_DIALOG = "editTextDateDialog";
    private static final String REMINDER_DATE_TIME_DIALOG = "reminderDateTimeDialog";

    //Reminder
    private boolean isUserSetReminder;
    private Date reminderDate;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


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

        //Reminder...
        reminderButton = (ImageButton) findViewById(R.id.ic_reminder);
        isUserSetReminder = false;

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, ReminderBroadCastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);



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
            boolean isCurrentDay = isCurrentDay(year, month, day);
            showTimePickerDialog(year, month, day, isCurrentDay);
        }
    }

    private boolean isCurrentDay(int year, int month, int day){
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        if(year == currentYear && month == currentMonth && day == currentDay){
            return true;
        }
        return false;
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

    private void showTimePickerDialog(final int year, final int month, final int day, final boolean isCurrentDay){
        final Calendar c = Calendar.getInstance();
        final int currentHour = c.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = c.get(Calendar.MINUTE);
        final int monthPlusOne = month + 1;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                try {
                    if( isCurrentDay ){
                        if( isUserTimeAfterCurrentTime(hour, minute) ){
                            Toast.makeText(CreateBagActivity.this,
                                    "Alarm set to " + day + "/" + (monthPlusOne) + "/" + year + " at " + hour + "h : " + minute + "m",
                                    Toast.LENGTH_LONG).show();
                            setReminderDateTime(year, month, day, hour, minute);
                        }
                        else{
                            Toast.makeText(CreateBagActivity.this,"Invalid time. Please, set future reminder time.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(CreateBagActivity.this,
                                "Alarm set to " + day + "/" + (monthPlusOne) + "/" + year + " at " + hour + "h : " + minute + "m",
                                Toast.LENGTH_LONG).show();
                        setReminderDateTime(year, month, day, hour, minute);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, currentHour, currentMinute, true);
        timePickerDialog.show();
    }

    private boolean isUserTimeAfterCurrentTime(int hour, int minute) throws ParseException {
        Calendar c = Calendar.getInstance();

        if (hour > c.get(Calendar.HOUR_OF_DAY)){
            return true;
        }

        if(hour == c.get(Calendar.HOUR_OF_DAY) && minute > c.get(Calendar.MINUTE )){
            return true;
        }
        return false;
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

    //REMINDER
    private void setReminderDateTime(int year, int month, int day, int hour, int minute) {

        LocalDateTime currenteDate = LocalDateTime.now();
        LocalDateTime reminderDate = LocalDateTime.of(year, Month.of(month+1), day, hour, minute);

        long p2 = ChronoUnit.SECONDS.between(currenteDate, reminderDate);

        Toast.makeText(CreateBagActivity.this,
                "p2 " + p2, Toast.LENGTH_LONG).show();

    }

    private void createReminder(){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, 0, pendingIntent);
        }

    }



}//endClass...