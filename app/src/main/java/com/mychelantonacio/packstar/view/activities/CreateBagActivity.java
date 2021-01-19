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
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.util.Dialogs.DatePickerFragmentDialog;
import com.mychelantonacio.packstar.util.Dialogs.DiscardChangesFragmentDialog;
import com.mychelantonacio.packstar.util.Dialogs.ReminderFragmentDialog;
import com.mychelantonacio.packstar.util.filters.DecimalDigitsInputFilter;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


public class CreateBagActivity extends AppCompatActivity
        implements DiscardChangesFragmentDialog.NoticeDialogListener,
        DatePickerFragmentDialog.DatePickerFragmentListener,
        ReminderFragmentDialog.NoticeDialogListener {


    //Dialogs
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
    private long NO_EVENT_SET = -1L;
    private long reminderEventId = NO_EVENT_SET;
    private boolean isEventSet = false;
    private long CALENDAR_NOT_FOUND = -10L;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_bag);
        if (savedInstanceState != null) {
            setReminderOnCreate(savedInstanceState);
        }
        setupUIOnCreate();
    }

    private void setReminderOnCreate(Bundle savedInstanceState) {
        this.isEventSet = savedInstanceState.getBoolean("isEventSet");
        this.reminderEventId = savedInstanceState.getLong("globalEventID");
        this.reminderEditText.setText(savedInstanceState.getString("reminderEditText"));
    }

    private void setupUIOnCreate() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        inputFieldSetup();

        bagViewModel = new ViewModelProvider(this).get(BagViewModel.class);
    }

    private void inputFieldSetup() {
        nameEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_name);
        dateEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_date);
        weightEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_weight);
        weightEditText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2,3)});
        commentEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_comment);

        dateTextInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout_bag_date);
        dateEditTextSetup();

        reminderEditText = (TextView) findViewById(R.id.textView_no_reminders);

        reminderButton = (ImageButton) findViewById(R.id.ic_reminder);
        reminderSetup();

        eFab = (ExtendedFloatingActionButton) findViewById(R.id.floatingActionButton);
        fabSetup();
    }

    private void dateEditTextSetup() {
        dateEditText.setOnClickListener(v -> {
            DATE_DIALOG = EDIT_TEXT_DATE_DIALOG;
            openDialog();
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

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            this.isEventSet = savedInstanceState.getBoolean("isEventSet");
            this.reminderEventId = savedInstanceState.getLong("globalEventID");
            this.reminderEditText.setText(savedInstanceState.getString("reminderEditText"));
        }
    }

    private void fabSetup() {
        eFab.setOnClickListener(v -> save());
    }

    private void reminderSetup() {
        reminderButton.setOnClickListener(v -> {
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
        });

        reminderEditText.setOnClickListener(v -> {
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
        });
    }

    private void save() {
        Bag newBag;

        if (isNameEmpty() || isDateEmpty()) {
            return;
        }

        newBag = setupBagToSave();

        if (this.isEventSet)
            setupEventToSave(newBag);

        bagViewModel.insert(newBag);

        Intent intent = new Intent(CreateBagActivity.this, ListBagActivity.class);
        startActivity(intent);
        finishAffinity();
        finish();
    }

    private Bag setupBagToSave() {
        Bag newBag = new Bag();
        newBag.setName(nameEditText.getText().toString());
        newBag.setTravelDate(dateEditText.getText().toString());
        if (!TextUtils.isEmpty(weightEditText.getText().toString())) {
            newBag.setWeight(new Double(weightEditText.getText().toString()));
        }
        newBag.setComment(commentEditText.getText().toString());
        return newBag;
    }

    private void setupEventToSave(Bag newBag) {
        newBag.setEventSet(true);
        newBag.setEventId(this.reminderEventId);
        newBag.setEventDateTime(reminderEditText.getText().toString());
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
    public void onDialogPositiveClick(androidx.fragment.app.DialogFragment dialog) {
        this.finish();
    }

    @Override
    public void onDialogNegativeClick(androidx.fragment.app.DialogFragment dialog) {
        dialog.dismiss();
    }

    private boolean isAnyFieldFilled(){
        if (!nameEditText.getText().toString().isEmpty() || !dateEditText.getText().toString().isEmpty() ||
                !weightEditText.getText().toString().isEmpty() || !commentEditText.getText().toString().isEmpty() ){
            return true;
        }
        return false;
    }

    //Date and TimeDate dialogs
    @Override
    public void onDateSet(int year, int month, int day) {
        if (DATE_DIALOG == EDIT_TEXT_DATE_DIALOG) {
            dateEditText.setText(formatEditTextDate(year, month, day));
        } else if (DATE_DIALOG == REMINDER_DATE_TIME_DIALOG) {
            boolean isCurrentDay = isCurrentDay(year, month, day);
            showTimePickerDialog(year, month, day, isCurrentDay);
        }
    }
    private String formatEditTextDate(int year, int month, int day){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currentDate = LocalDate.of(year, ++month, day);
        return currentDate.format(dateFormatter);
    }

    private boolean isCurrentDay(int year, int month, int day) {
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        int currentDay = LocalDate.now().getDayOfMonth();

        ++month;//datepicker 0 index based
        if (year == currentYear && month == currentMonth && day == currentDay) {
            return true;
        }
        return false;
    }

    public void openDialog() {
        datePickerFragmentDialog = new DatePickerFragmentDialog();
        datePickerFragmentDialog.show(getSupportFragmentManager(), DIALOG_DATE_PICKER);
    }

    private boolean isNameEmpty() {
        String bagName = nameEditText.getText().toString().trim();
        String alertMessage = getResources().getString(R.string.alert_create_bag_name_required);
        if (TextUtils.isEmpty(bagName)) {
            nameEditText.setError(alertMessage);
            return true;
        }
        return false;
    }

    private boolean isDateEmpty() {
        String bagDate = dateEditText.getText().toString();
        String alertMessage = getResources().getString(R.string.alert_create_bag_date_required);
        if (TextUtils.isEmpty(bagDate)) {
            dateEditText.setError(alertMessage);
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("isEventSet", this.isEventSet);
        savedInstanceState.putLong("globalEventID", reminderEventId);
        savedInstanceState.putString("reminderEditText", this.reminderEditText.getText().toString());
    }

    //Calendar
    private void showTimePickerDialog(final int year, final int month, final int day, final boolean isCurrentDay) {
        final Calendar c = Calendar.getInstance();
        final int currentHour = c.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hour, minute) -> {
            try {
                if (isCurrentDay) {
                    if (isUserTimeAfterCurrentTime(hour, minute)) {
                        setReminderDateTime(year, month, day, hour, minute);
                    } else {
                        Toast.makeText(CreateBagActivity.this, getResources().getString(R.string.reminder_create_bag_invalid_time), Toast.LENGTH_LONG).show();
                    }
                } else {
                    setReminderDateTime(year, month, day, hour, minute);
                }
            } catch (ParseException e) {
                e.printStackTrace();
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

    //Calendar
    private void setReminderDateTime(int year, int month, int day, int hour, int minute) {

        long startMillis = 0L;
        long endMillis = 0L;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            long calendarIdResult = getCalendarId(this);
            if (calendarIdResult == CALENDAR_NOT_FOUND)
                Toast.makeText(CreateBagActivity.this, getResources().getString(R.string.reminder_calendar_not_found), Toast.LENGTH_LONG).show();

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
            values.put(CalendarContract.Events.TITLE, nameEditText.getText().toString() == null ? "PackStar" : nameEditText.getText().toString());
            values.put(CalendarContract.Events.DESCRIPTION, commentEditText.getText().toString() == null ? getResources().getString(R.string.reminder_create_bag_trip_is_coming) : commentEditText.getText().toString());
            values.put(CalendarContract.Events.CALENDAR_ID, calendarIdResult);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/London");

            //create event
            if (this.reminderEventId == NO_EVENT_SET) {
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                long eventID = Long.parseLong(uri.getLastPathSegment());
                Toast.makeText(CreateBagActivity.this, getResources().getString(R.string.reminder_create_bag_add_success), Toast.LENGTH_SHORT).show();
                this.isEventSet = true;
                this.reminderEventId = eventID;
                this.reminderEditText.setText(formatReminderDateTime(year, month, day, hour, minute));
            }
            //update event
            else {
                if (!isEventExistOnCalendar()) {
                    this.isEventSet = false;
                    this.reminderEventId = NO_EVENT_SET;
                    this.reminderEditText.setText(getResources().getString(R.string.reminder_none));
                    Toast.makeText(CreateBagActivity.this, getResources().getString(R.string.reminder_create_bag_update_delete_failure), Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri updateUri = null;
                updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, this.reminderEventId);
                cr.update(updateUri, values, null, null);
                this.reminderEditText.setText(formatReminderDateTime(year, month, day, hour, minute));
                Toast.makeText(CreateBagActivity.this, getResources().getString(R.string.reminder_create_bag_update_success), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR) &&
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CALENDAR)) {
                Toast.makeText(CreateBagActivity.this, getResources().getString(R.string.reminder_permission_read_write), Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, REQUEST_PERMISSION_READ_WRITE_CALENDAR);
        }
    }

    private String formatReminderDateTime(int year, int month, int day, int hour, int minute) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate currentDate = LocalDate.of(year, ++month, day);
        String formattedDate = currentDate.format(dateFormatter);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime currentTime = LocalTime.of(hour, minute);
        String formattedTime = currentTime.format(timeFormatter);

        return formattedDate + " " + formattedTime;
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
            //int PROJECTION_ACCOUNT_NAME_INDEX = 1;
            //int PROJECTION_DISPLAY_NAME_INDEX = 2;
            //int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
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
                return CALENDAR_NOT_FOUND;
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR)) {
                Toast.makeText(CreateBagActivity.this, getResources().getString(R.string.reminder_permission_read_write), Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_PERMISSION_READ_WRITE_CALENDAR);
        }
        return CALENDAR_NOT_FOUND;
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
        int deletedRows = cr.delete(deleteUri, null, null);

        if (deletedRows == 0) {
            Toast.makeText(CreateBagActivity.this, getResources().getString(R.string.reminder_create_bag_update_delete_failure), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CreateBagActivity.this, getResources().getString(R.string.reminder_create_bag_delete_success), Toast.LENGTH_SHORT).show();
        }
        this.reminderEventId = NO_EVENT_SET;
        this.isEventSet = false;
        this.reminderEditText.setText(getResources().getString(R.string.reminder_none));
    }

    private void editReminderEvent() {
        DATE_DIALOG = REMINDER_DATE_TIME_DIALOG;
        openDialog();
    }

    private boolean isEventExistOnCalendar() {
        boolean isEventFound = false;
        long selectedEventId = this.reminderEventId;

        String[] proj =
                new String[]{
                        CalendarContract.Events._ID,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                        CalendarContract.Events.RRULE,
                        CalendarContract.Events.TITLE,
                        CalendarContract.Events.DELETED
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                                                         proj,
                                                CalendarContract.Events._ID + " = ? ",
                                                         new String[]{Long.toString(selectedEventId)},
                                                null);

            if (cursor != null  && cursor.getCount() > 0) {
                if(cursor.moveToFirst()){
                    if(cursor.getString(5).equals("0"))
                        isEventFound = true;
                }
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR)) {
                Toast.makeText(CreateBagActivity.this, getResources().getString(R.string.reminder_permission_read_write), Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_PERMISSION_READ_WRITE_CALENDAR);
        }
        return isEventFound;
    }
}