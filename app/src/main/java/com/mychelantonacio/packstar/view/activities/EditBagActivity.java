package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.util.Dialogs.DatePickerFragmentDialog;
import com.mychelantonacio.packstar.util.Dialogs.DiscardChangesFragmentDialog;
import com.mychelantonacio.packstar.util.Dialogs.ReminderFragmentDialog;

import com.mychelantonacio.packstar.util.filters.DecimalDigitsInputFilter;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


public class EditBagActivity extends AppCompatActivity
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
    private com.google.android.material.textfield.TextInputLayout nameTextInputLayout;
    private TextInputEditText dateEditText;
    private com.google.android.material.textfield.TextInputLayout dateTextInputLayout;
    private TextInputEditText weightEditText;
    private com.google.android.material.textfield.TextInputLayout weightTextInputLayout;
    private TextInputEditText commentEditText;
    private com.google.android.material.textfield.TextInputLayout commentTextInputLayout;

    private TextView reminderEditText;
    private ImageButton reminderButton;
    private ExtendedFloatingActionButton eFab;

    //Data
    private Bag currentBag;
    private Bag originalBag;
    private BagViewModel bagViewModel;
    private ItemViewModel itemViewModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bag);
        if (savedInstanceState != null) {
            this.isEventSet = savedInstanceState.getBoolean("isEventSet");
            this.reminderEventId = savedInstanceState.getLong("globalEventID");
            this.reminderEditText.setText(savedInstanceState.getString("reminderEditText"));
        }
        setupUIOnCreate();
    }

    private void setupUIOnCreate() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        nameEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_name);
        nameTextInputLayout = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.textInputLayout_bag_name);
        dateEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_date);
        dateTextInputLayout = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.textInputLayout_bag_date);
        weightEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_weight);
        weightEditText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2,3)});
        weightTextInputLayout = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.textInputLayout_bag_weight);
        commentEditText = (TextInputEditText) findViewById(R.id.textInputEditText_bag_comment);
        commentTextInputLayout = (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.textInputLayout_bag_comment);
        reminderButton = (ImageButton) findViewById(R.id.ic_reminder);
        reminderEditText = (TextView) findViewById(R.id.textView_no_reminders);
        reminderSetup();

        eFab = (ExtendedFloatingActionButton) findViewById(R.id.floatingActionButton);
        fabSetup();

        Intent intent = getIntent();
        currentBag = (Bag) intent.getParcelableExtra("bag_parcelable");
        originalBag = currentBag;

        if(currentBag.isEventSet()){
            this.isEventSet = currentBag.isEventSet();
            this.reminderEventId = currentBag.getEventId();
            reminderEditText.setText(currentBag.getEventDateTime())  ;
        }

        nameEditText.setText(currentBag.getName());
        nameTextInputLayout.setEndIconVisible(false);

        dateEditText.setText(currentBag.getTravelDate());
        dateTextInputLayout.setEndIconVisible(false);
        dateEditTextSetup();

        weightEditText.setText(String.valueOf(currentBag.getWeight() == null ? "" : currentBag.getWeight() ));
        weightTextInputLayout.setEndIconVisible(false);

        commentEditText.setText(String.valueOf(currentBag.getComment() == null ? "" : currentBag.getComment()));
        commentTextInputLayout.setEndIconVisible(false);

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        bagViewModel = new ViewModelProvider(this).get(BagViewModel.class);
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

    private void save(){
        if (isNameEmpty() || isDateEmpty()) {
            return;
        }

        currentBag.setName(nameEditText.getText().toString());
        currentBag.setTravelDate(dateEditText.getText().toString());
        if(!TextUtils.isEmpty(weightEditText.getText().toString())){
            currentBag.setWeight(new Double(weightEditText.getText().toString()));
        }
        currentBag.setComment(commentEditText.getText().toString());

        if (this.isEventSet) {
            currentBag.setEventSet(true);
            currentBag.setEventId(this.reminderEventId);
            currentBag.setEventDateTime(reminderEditText.getText().toString());
        }
        else{
            currentBag.setEventSet(false);
            currentBag.setEventId(NO_EVENT_SET);
            currentBag.setEventDateTime(getResources().getString(R.string.reminder_none));
        }

        bagViewModel.update(currentBag);

        nameEditText.setText("");
        dateEditText.setText("");
        weightEditText.setText("");
        commentEditText.setText("");

        Intent intent = new Intent(EditBagActivity.this, ListBagActivity.class);
        startActivity(intent);
        finish();
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

    private void fabSetup(){
        eFab.setOnClickListener(v -> save());
    }

    private void reminderSetup() {
        reminderButton.setOnClickListener(v -> {
            if (EditBagActivity.this.isEventSet) {
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
            if (EditBagActivity.this.isEventSet) {
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

    //back button
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(isAnyFieldChanged()) {
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

        //NEW ONE -> DELETE IT
        if(!originalBag.isEventSet()){
            ContentResolver cr = getContentResolver();
            Uri deleteUri = null;
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, this.reminderEventId);
            int deletedRows = cr.delete(deleteUri, null, null);

            if (deletedRows == 0) {
                Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_create_bag_update_delete_failure), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_create_bag_delete_success), Toast.LENGTH_SHORT).show();
            }
        }
        //EXISTING ONE
        else {
            int day;
            int month;
            int year;
            int hour;
            int minute;

            int [] dateTimeSplit = dateTimeToInt(originalBag.getEventDateTime());
            if(dateTimeSplit.length == 5){
                day = dateTimeSplit[0];
                month = dateTimeSplit[1];
                year = dateTimeSplit[2];
                hour = dateTimeSplit[3];
                minute = dateTimeSplit[4];
            }
            else{
                day = 1;
                month = 1;
                year = 2000;
                hour = 1;
                minute = 1;
            }

            long startMillis = 0L;
            long endMillis = 0L;

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

                long calendarIdResult = getCalendarId(this);
                if (calendarIdResult == CALENDAR_NOT_FOUND)
                    Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_calendar_not_found), Toast.LENGTH_LONG).show();

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
                values.put(CalendarContract.Events.TITLE, nameEditText.getText().toString() == null ? "PackStar" : nameEditText.getText().toString());
                values.put(CalendarContract.Events.DESCRIPTION, commentEditText.getText().toString() == null ? getResources().getString(R.string.reminder_create_bag_trip_is_coming) : commentEditText.getText().toString());
                values.put(CalendarContract.Events.CALENDAR_ID, calendarIdResult);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/London");

                //EXISTING ONE BUT DELETED -> CREATE NEW ONE WITHIN ORIGINAL DATA
                if(!this.isEventSet ){
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                    long eventID = Long.parseLong(uri.getLastPathSegment());
                    Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_create_bag_add_success), Toast.LENGTH_SHORT).show();
                    this.isEventSet = true;
                    this.reminderEventId = eventID;
                    this.reminderEditText.setText(formatReminderDateTime(year, month, day, hour, minute));
                    currentBag = originalBag;
                    currentBag.setEventId(eventID);
                    bagViewModel.update(currentBag);
                }
                //EXISTING ONE BUT UPDATED -> UPDATE WITHIN ORIGINAL DATA
                else{
                    if (!isEventExistOnCalendar(true)) {
                        this.finish();
                    }
                    else{
                        Uri updateUri = null;
                        updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, this.reminderEventId);
                        cr.update(updateUri, values, null, null);
                        this.reminderEditText.setText(formatReminderDateTime(year, month, day, hour, minute));
                    }
                }
            }
        }
        this.finish();
    }

    @Override
    public void onDialogNegativeClick(androidx.fragment.app.DialogFragment dialog) {
        dialog.dismiss();
    }

    private boolean isAnyFieldChanged(){
        if (!nameEditText.getText().toString().equals(originalBag.getName()) ||
                !dateEditText.getText().toString().equals(originalBag.getTravelDate()) ||
                !weightEditText.getText().toString().equals(originalBag.getWeight() == null ? "" : originalBag.getWeight().toString()) ||
                !commentEditText.getText().toString().equals(originalBag.getComment() == null ? "" : originalBag.getComment()) ||
                isReminderChanged()){
            return true;
        }
        return false;
    }

    private boolean isReminderChanged(){
        long reminderEventIdOriginalBag = originalBag.getEventId() == 0 ? NO_EVENT_SET : originalBag.getEventId();
        String reminderEditTextOriginalBag = originalBag.getEventDateTime() == null ? getResources().getString(R.string.reminder_none) : originalBag.getEventDateTime();
        if(this.isEventSet != originalBag.isEventSet() ||
                this.reminderEventId != reminderEventIdOriginalBag ||
                !this.reminderEditText.getText().equals(reminderEditTextOriginalBag) ){
            return true;
        }
        return false;
    }

    private int[] dateTimeToInt(String dateTime){
        int[] dateTimeSplit = new int[5];
        String[] date = dateTime.split("/");

        dateTimeSplit[0] = Integer.parseInt(date[0]);//day
        dateTimeSplit[1] = Integer.parseInt(date[1]);//month
        dateTimeSplit[2] = Integer.parseInt(date[2].substring(0, 4));//year
        dateTimeSplit[3] = Integer.parseInt(date[2].substring(6, 8));//hour
        dateTimeSplit[4] = Integer.parseInt(date[2].substring(9, 11));//minute
        return dateTimeSplit;
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

    public void showDatePickerReminderDialog(View v) {
        DATE_DIALOG = REMINDER_DATE_TIME_DIALOG;
        openDialog();
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
                        Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_create_bag_invalid_time), Toast.LENGTH_LONG).show();
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

    private void setReminderDateTime(int year, int month, int day, int hour, int minute) {

        long startMillis = 0L;
        long endMillis = 0L;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {

            long calendarIdResult = getCalendarId(this);
            if (calendarIdResult == CALENDAR_NOT_FOUND)
                Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_calendar_not_found), Toast.LENGTH_LONG).show();

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
                Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_create_bag_add_success), Toast.LENGTH_SHORT).show();
                this.isEventSet = true;
                this.reminderEventId = eventID;
                this.reminderEditText.setText(formatReminderDateTime(year, month, day, hour, minute));
            }
            //update event
            else {
                if (!isEventExistOnCalendar(false)) {
                    this.isEventSet = false;
                    this.reminderEventId = NO_EVENT_SET;
                    this.reminderEditText.setText(getResources().getString(R.string.reminder_none));
                    Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_create_bag_update_delete_failure), Toast.LENGTH_SHORT).show();
                    return;
                }
                Uri updateUri = null;
                updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, this.reminderEventId);
                cr.update(updateUri, values, null, null);
                this.reminderEditText.setText(formatReminderDateTime(year, month, day, hour, minute));
                Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_create_bag_update_success), Toast.LENGTH_SHORT).show();
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR) &&
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CALENDAR)) {
                Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_permission_read_write), Toast.LENGTH_LONG).show();
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
                return CALENDAR_NOT_FOUND;
            }
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CALENDAR)) {
                Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_permission_read_write), Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_PERMISSION_READ_WRITE_CALENDAR);
        }
        return CALENDAR_NOT_FOUND;
    }

    private boolean isEventExistOnCalendar(boolean isRollBack) {
        boolean isEventFound = false;
        long selectedEventId;

        if(isRollBack){
            selectedEventId = originalBag.getEventId();
        }
        else{
            selectedEventId = this.reminderEventId;
        }

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
                Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_permission_read_write), Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, REQUEST_PERMISSION_READ_WRITE_CALENDAR);
        }
        return isEventFound;
    }


    @Override
    public void onDialogEditClick(androidx.fragment.app.DialogFragment dialog) { editReminderEvent(); }

    private void editReminderEvent() {
        DATE_DIALOG = REMINDER_DATE_TIME_DIALOG;
        openDialog();
    }

    @Override
    public void onDialogDeleteClick(androidx.fragment.app.DialogFragment dialog) {
        ContentResolver cr = getContentResolver();
        Uri deleteUri = null;
        deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, this.reminderEventId);
        int deletedRows = cr.delete(deleteUri, null, null);

        if (deletedRows == 0) {
            Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_create_bag_update_delete_failure), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EditBagActivity.this, getResources().getString(R.string.reminder_create_bag_delete_success), Toast.LENGTH_SHORT).show();
        }
        this.reminderEventId = NO_EVENT_SET;
        this.isEventSet = false;
        this.reminderEditText.setText(getResources().getString(R.string.reminder_none));
    }
}
