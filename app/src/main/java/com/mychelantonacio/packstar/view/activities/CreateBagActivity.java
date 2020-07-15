package com.mychelantonacio.packstar.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.concurrent.TimeUnit;


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

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";


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


        //prePopulateForTestingPurpose();
        Intent intent = new Intent(CreateBagActivity.this, ListBagActivity.class);
        startActivity(intent);
    }

    private void prePopulateForTestingPurpose() {
        //bagViewModel.deleteAll();
        for (int i = 1; i <= 100; i++) {
            // Bag bag = new Bag("Test Bag " + i, "01/01/2020", new Double(i), "Test Comment " + i);
            //bagViewModel.insert(bag);

            for (int j = 1; j <= 3; j++) {
                Item item = new Item();

                item.setBagId(new Long(i));
                item.setName("Item " + j);
                item.setQuantity(j);
                item.setWeight(new Double(j));

                if (j % 2 == 0)
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

        DATE_DIALOG = REMINDER_DATE_TIME_DIALOG;
        openDialog();
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
                            //Toast.makeText(CreateBagActivity.this,
                            //      "Alarm set to " + day + "/" + (monthPlusOne) + "/" + year + " at " + hour + "h : " + minute + "m",
                            //    Toast.LENGTH_LONG).show();
                            setReminderDateTime(year, month, day, hour, minute);
                        } else {
                            Toast.makeText(CreateBagActivity.this, "Invalid time. Please, set future reminder time.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //Toast.makeText(CreateBagActivity.this,
                        //      "Alarm set to " + day + "/" + (monthPlusOne) + "/" + year + " at " + hour + "h : " + minute + "m",
                        //    Toast.LENGTH_LONG).show();
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


        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2020, 8, 15, 8, 0);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2020, 8, 15, 9, 0);
        endMillis = endTime.getTimeInMillis();


        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "PackStar - Test");
        values.put(CalendarContract.Events.DESCRIPTION, "Testing");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);


        long eventID = Long.parseLong(uri.getLastPathSegment());
        Log.d("eventID", "eventID " + eventID);



















        /* VIEW
        long eventID = 6666L;

        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(uri);
        startActivity(intent);
*/


/* EDIT
        long eventID = 111;
        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        Intent intent = new Intent(Intent.ACTION_EDIT)
                .setData(uri);
                //.putExtra(CalendarContract.Events.TITLE, "Editing bolado!");

        startActivity(intent);
*/


/* INSERT

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(year, month, day, hour, minute);
        Calendar endTime = Calendar.getInstance();
        endTime.set(year, month, day, (hour+1), minute);

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, "PackStar")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Your trip is coming soon!");

        startActivity(intent);
*/

        /* ALARM + NOTIFICATION...
        LocalDateTime currenteDate = LocalDateTime.now();
        LocalDateTime reminderDate = LocalDateTime.of(year, Month.of(month+1), day, hour, minute);

        long futureTimeInSeconds = ChronoUnit.SECONDS.between(currenteDate, reminderDate);
        //createReminder(futureTimeInSeconds);

        createAlarmViaWorkManager(futureTimeInSeconds);

         */
    }








    private void createReminder(long futureTimeInSeconds ){
        scheduleNotification(getNotification( "Your trip is coming soon!") , futureTimeInSeconds ) ;
    }

    private Notification getNotification (String content) {

        Intent intent = new Intent(this, ListBagActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, default_notification_channel_id)
                .setSmallIcon(R.drawable.splashscreen_image)
                .setContentTitle("Packstar Notification")
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setChannelId( NOTIFICATION_CHANNEL_ID )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        return builder.build() ;
    }

    private void scheduleNotification (Notification notification, long delay) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE );

        Intent notificationIntent = new Intent( this, ReminderBroadcastReceiver.class );
        notificationIntent.putExtra(ReminderBroadcastReceiver.NOTIFICATION_ID, 1 );
        notificationIntent.putExtra(ReminderBroadcastReceiver.NOTIFICATION, notification);


        //PendingIntent.FLAG_CANCEL_CURRENT
        notificationIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        PendingIntent pendingIntent = PendingIntent.getBroadcast ( this, 0 , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT );
        assert alarmManager != null;
        long ALARM_DELAY_IN_SECOND = 60;
        long alarmTimeAtUTC = System.currentTimeMillis() + ALARM_DELAY_IN_SECOND * 1_000L;

        long delayAlarmTimeAtUTC = System.currentTimeMillis() + delay * 1_000L;
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, delayAlarmTimeAtUTC, pendingIntent);
    }


    private void createAlarmViaWorkManager(long delay){

        Log.d("jojoba", "delay " + delay);

        long delayAlarmTimeAtUTC = System.currentTimeMillis() + delay * 1_000L;


        //we set a tag to be able to cancel all work of this type if needed
        final String workTag = "notificationWork";

        //store DBEventID to pass it to the PendingIntent and open the appropriate event page on notification click
        //Data inputData = new Data.Builder().putInt(DBEventIDTag, DBEventID).build();

        Data inputData = new Data.Builder().putLong("delay", delayAlarmTimeAtUTC).build();

        // we then retrieve it inside the NotifyWorker with:
        // final int DBEventID = getInputData().getInt(DBEventIDTag, ERROR_VALUE);



        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest.Builder(NotifyWorker.class)
                .setInitialDelay(delay * 1_000, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(workTag)
                .build();

        WorkManager.getInstance(this).enqueue(notificationWork);
    }

}