package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
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
    private long globalEventID = 0L;
    private boolean isEventSet = false;
    private static final int REQUEST_PERMISSION_WRITE_CALENDAR = 007;

    //Widgets
    private TextInputEditText nameEditText;
    private TextInputEditText dateEditText;
    private TextInputEditText weightEditText;
    private TextInputEditText commentEditText;
    private TextView reminderEditText;
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

        /*
        if(savedInstanceState != null){
            this.isEventSet = savedInstanceState.getBoolean("isEventSet");
            this.globalEventID = savedInstanceState.getLong("globalEventID");
            this.reminderEditText.setText(savedInstanceState.getString("reminderEditText"));
        }
         */
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            this.isEventSet = savedInstanceState.getBoolean("isEventSet");
            this.globalEventID = savedInstanceState.getLong("globalEventID");
            this.reminderEditText.setText(savedInstanceState.getString("reminderEditText"));
        }
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
        reminderEditText = (TextView) findViewById(R.id.textView_no_reminders);
        dateEditTextSetup();

        //Reminder...
        reminderButton = (ImageButton) findViewById(R.id.ic_reminder);
        //TODO: setup reminderButton...
        eFab = (ExtendedFloatingActionButton) findViewById(R.id.floatingActionButton);
        fabSetup();
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        bagViewModel = new ViewModelProvider(this).get(BagViewModel.class);
    }

    //it avoids conflict with datepicker action...
    private void dateEditTextSetup() {
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

    private void fabSetup() {
        eFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBag();
            }
        });
    }

    private void createBag() {
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
        Intent intent = new Intent(CreateBagActivity.this, ListBagActivity.class);
        startActivity(intent);
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
        this.finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    //Date and TimeDate dialogs
    @Override
    public void onDateSet(int year, int month, int day) {
        if (DATE_DIALOG == EDIT_TEXT_DATE_DIALOG) {
            dateEditText.setText(day + "/" + (++month) + "/" + year);
        } else if (DATE_DIALOG == REMINDER_DATE_TIME_DIALOG) {
            boolean isCurrentDay = isCurrentDay(year, month, day);
            showTimePickerDialog(year, month, day, isCurrentDay);
        }
    }

    private boolean isCurrentDay(int year, int month, int day) {
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        if (year == currentYear && month == currentMonth && day == currentDay) {
            return true;
        }
        return false;
    }

    public void showDatePickerDialog(View v) {
        DATE_DIALOG = EDIT_TEXT_DATE_DIALOG;
        openDialog();
    }

    public void openDialog() {
        DatePickerFragmentDialog datePickerFragmentDialog = new DatePickerFragmentDialog();
        datePickerFragmentDialog.show(getSupportFragmentManager(), DIALOG_DATE_PICKER);
    }

    public void showDatePickerReminderDialog(View v) {
        if (this.isEventSet){
            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, this.globalEventID);
            Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);
            startActivity(intent);
        }
        else{
            DATE_DIALOG = REMINDER_DATE_TIME_DIALOG;
            openDialog();
        }
    }

    private void showTimePickerDialog(final int year, final int month, final int day, final boolean isCurrentDay) {
        final Calendar c = Calendar.getInstance();
        final int currentHour = c.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = c.get(Calendar.MINUTE);
        final int monthPlusOne = month + 1;

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                try {
                    if (isCurrentDay) {
                        if (isUserTimeAfterCurrentTime(hour, minute)) {
                            setReminderDateTime(year, month, day, hour, minute);
                        } else {
                            Toast.makeText(CreateBagActivity.this, "Invalid time. Please, set future reminder time.", Toast.LENGTH_LONG).show();
                        }
                    } else {
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
        if (hour > c.get(Calendar.HOUR_OF_DAY)) {
            return true;
        }
        if (hour == c.get(Calendar.HOUR_OF_DAY) && minute > c.get(Calendar.MINUTE)) {
            return true;
        }
        return false;
    }

    private boolean isNameEmpty() {
        String bagName = nameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(bagName)) {
            nameEditText.setError("Please, enter Bag name");
            return true;
        }
        return false;
    }

    private boolean isDateEmpty() {
        String bagDate = dateEditText.getText().toString();
        if (TextUtils.isEmpty(bagDate)) {
            dateEditText.setError("Please, enter Date name");
            return true;
        }
        return false;
    }

    //REMINDER
    private void setReminderDateTime(int year, int month, int day, int hour, int minute) {

        //TODO: get range of calID from user system...
        long calID = 1L;
        long startMillis = 0;
        long endMillis = 0;

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, hour, minute);
        startMillis = beginTime.getTimeInMillis();

        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, (hour+1), minute);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "PackStar");
        values.put(CalendarContract.Events.DESCRIPTION, "Your trip is coming soon!");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/London");


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            long eventID = Long.parseLong(uri.getLastPathSegment());
            Log.d("TAG", "uri " + uri.toString());
            Log.d("TAG", "eventID " + eventID);
            Toast.makeText(CreateBagActivity.this, "Success! Your event was added to your calendar.  ", Toast.LENGTH_LONG).show();
            this.isEventSet = true;
            this.globalEventID = eventID;
            this.reminderEditText.setText("Reminder already set");
        }
        else{
            if( shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CALENDAR) ){
                Toast.makeText(CreateBagActivity.this, "Write to calendar permission is needed to create reminders", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_CALENDAR}, REQUEST_PERMISSION_WRITE_CALENDAR);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("isEventSet", this.isEventSet);
        savedInstanceState.putLong("globalEventID", this.globalEventID);
        savedInstanceState.putString("reminderEditText", this.reminderEditText.getText().toString());
    }
}