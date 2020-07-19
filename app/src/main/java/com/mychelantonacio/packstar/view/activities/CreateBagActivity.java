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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.util.Dialogs.DatePickerFragmentDialog;
import com.mychelantonacio.packstar.util.Dialogs.DiscardChangesFragmentDialog;
import com.mychelantonacio.packstar.util.Dialogs.ReminderFragmentDialog;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;

import java.text.ParseException;
import java.util.Calendar;


public class CreateBagActivity extends AppCompatActivity
        implements DiscardChangesFragmentDialog.NoticeDialogListener,
        DatePickerFragmentDialog.DatePickerFragmentListener,
        ReminderFragmentDialog.NoticeDialogListener{


    private DiscardChangesFragmentDialog discardChangesFragmentDialog;
    private static final String DIALOG_DISCARD = "DiscardChangesFragmentDialog";
    private DatePickerFragmentDialog datePickerFragmentDialog;
    private static final String DIALOG_DATE_PICKER = "DatePickerFragmentDialog";
    private ReminderFragmentDialog reminderFragmentDialog;
    private static final String DIALOG_REMINDER = "ReminderFragmentDialog";

    private String DATE_DIALOG;
    private static final String EDIT_TEXT_DATE_DIALOG = "editTextDateDialog";
    private static final String REMINDER_DATE_TIME_DIALOG = "reminderDateTimeDialog";

    //Reminder
    private long NO_REMINDERS = -1L;
    private long reminderEventId = NO_REMINDERS;
    private boolean isEventSet = false;
    private static final int REQUEST_PERMISSION_READ_WRITE_CALENDAR = 007;

    //Widgets
    private TextInputEditText nameEditText;
    private TextInputEditText dateEditText;
    private TextInputEditText weightEditText;
    private TextInputEditText commentEditText;
    private TextInputLayout dateTextInputLayout;
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
        if (savedInstanceState != null) {
            this.isEventSet = savedInstanceState.getBoolean("isEventSet");
            this.reminderEventId = savedInstanceState.getLong("globalEventID");
            this.reminderEditText.setText(savedInstanceState.getString("reminderEditText"));
        }
        setupUIOnCreate();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            this.isEventSet = savedInstanceState.getBoolean("isEventSet");
            this.reminderEventId = savedInstanceState.getLong("globalEventID");
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

        dateTextInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout_bag_date);

        reminderEditText = (TextView) findViewById(R.id.textView_no_reminders);
        dateEditTextSetup();

        reminderButton = (ImageButton) findViewById(R.id.ic_reminder);
        reminderSetup();

        eFab = (ExtendedFloatingActionButton) findViewById(R.id.floatingActionButton);
        fabSetup();

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        bagViewModel = new ViewModelProvider(this).get(BagViewModel.class);
    }


    private void dateEditTextSetup() {
        dateEditText.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  DATE_DIALOG = EDIT_TEXT_DATE_DIALOG;
                  openDialog();
              }
          });
        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                dateTextInputLayout.setEndIconVisible(false);
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

    private void reminderSetup() {
        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CreateBagActivity.this.isEventSet) {
                    if (isNameEmpty() || isDateEmpty()) {
                        return;
                    }
                    reminderFragmentDialog = new ReminderFragmentDialog();
                    reminderFragmentDialog.show(getSupportFragmentManager(), DIALOG_REMINDER);

                } else {
                    if (isNameEmpty() || isDateEmpty()) {
                        return;
                    }
                    DATE_DIALOG = REMINDER_DATE_TIME_DIALOG;
                    openDialog();
                }
            }
        });
    }

    private void createBag() {

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

        if(this.isEventSet){
            newBag.setEventSet(true);
            newBag.setEventId(this.reminderEventId);
            newBag.setEventDateTime(reminderEditText.getText().toString());
        }

        bagViewModel.insert(newBag);
        Intent intent = new Intent(CreateBagActivity.this, ListBagActivity.class);
        startActivity(intent);
        finish();
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
    public void onDialogPositiveClick(androidx.fragment.app.DialogFragment dialog) {
        this.finish();
    }

    @Override
    public void onDialogNegativeClick(androidx.fragment.app.DialogFragment dialog) {
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

    public void openDialog() {
        datePickerFragmentDialog = new DatePickerFragmentDialog();
        datePickerFragmentDialog.show(getSupportFragmentManager(), DIALOG_DATE_PICKER);
    }

    private void showTimePickerDialog(final int year, final int month, final int day, final boolean isCurrentDay) {
        final Calendar c = Calendar.getInstance();
        final int currentHour = c.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = c.get(Calendar.MINUTE);

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            long startMillis = 0L;
            long endMillis = 0L;

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(year, month, day, hour, minute);
            startMillis = beginTime.getTimeInMillis();

            Calendar endTime = Calendar.getInstance();
            endTime.set(year, month, day, (hour + 1), minute);
            endMillis = endTime.getTimeInMillis();

            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();

            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "PackStar");
            values.put(CalendarContract.Events.DESCRIPTION, "Your trip is coming soon!");
            values.put(CalendarContract.Events.CALENDAR_ID, getCalendarId(this));
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/London");

            if(this.reminderEventId == NO_REMINDERS){
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

                long eventID = Long.parseLong(uri.getLastPathSegment());
                Toast.makeText(CreateBagActivity.this, "Success! Your reminder was added to your calendar", Toast.LENGTH_SHORT).show();

                this.isEventSet = true;
                this.reminderEventId = eventID;
                this.reminderEditText.setText( formatReminderDateTime(year, month, day, hour, minute) );
            }
            else{
                Uri updateUri = null;
                updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, this.reminderEventId);
                cr.update(updateUri, values, null, null);
                this.reminderEditText.setText( formatReminderDateTime(year, month, day, hour, minute) );
                Toast.makeText(CreateBagActivity.this, "Success! Your reminder was updated", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR) &&
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CALENDAR)) {
                Toast.makeText(CreateBagActivity.this, "Read/Write to the calendar permission is needed to create reminders", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, REQUEST_PERMISSION_READ_WRITE_CALENDAR);
        }
    }

    private String formatReminderDateTime(int year, int month, int day, int hour, int minute){

        if(month < 10 && minute < 10)
            return  day + "/" + "0" + month + "/" + year + "  " + hour + ":" + "0" + minute;
        else if(month < 10)
            return day + "/" + "0" + month + "/" + year + "  " + hour + ":" + minute;
        else
            return day + "/" + month + "/" + year + "  " + hour + ":" + minute;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("isEventSet", this.isEventSet);
        savedInstanceState.putLong("globalEventID", reminderEventId);
        savedInstanceState.putString("reminderEditText", this.reminderEditText.getText().toString());
    }


    public long getCalendarId(Context context) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = null;
            ContentResolver contentResolver = context.getContentResolver();
            Uri calendars = CalendarContract.Calendars.CONTENT_URI;

            String[] EVENT_PROJECTION = new String[]{
                    CalendarContract.Calendars._ID,                           // 0
                    CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                    CalendarContract.Calendars.OWNER_ACCOUNT,                 // 3
                    CalendarContract.Calendars.IS_PRIMARY                     // 4
            };

            int PROJECTION_ID_INDEX = 0;
            int PROJECTION_ACCOUNT_NAME_INDEX = 1;
            int PROJECTION_DISPLAY_NAME_INDEX = 2;
            int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
            int PROJECTION_VISIBLE = 4;

            cursor = contentResolver.query(calendars, EVENT_PROJECTION, null, null, null);

            if (cursor.moveToFirst()) {
                long calId = 0;
                String visible;
                do {
                    calId = cursor.getLong(PROJECTION_ID_INDEX);
                    visible = cursor.getString(PROJECTION_VISIBLE);
                    if (visible.equals("1")) {
                        return calId;
                    }
                } while (cursor.moveToNext());
                return calId;
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR)) {
                Toast.makeText(CreateBagActivity.this, "Read/Write to the calendar permission is needed to create reminders", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_PERMISSION_READ_WRITE_CALENDAR);
        }
        return 1L;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    public void onDialogEditClick(DialogFragment dialog) {
        editReminderEvent();
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        ContentResolver cr = getContentResolver();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, this.reminderEventId);
        cr.delete(deleteUri, null, null);
        this.reminderEventId = NO_REMINDERS;
        Toast.makeText(CreateBagActivity.this, "Success! Your reminder was deleted", Toast.LENGTH_SHORT).show();
        this.reminderEditText.setText("No reminders" );
    }


    private void editReminderEvent(){
        DATE_DIALOG = REMINDER_DATE_TIME_DIALOG;
        openDialog();
    }

}//endClass...