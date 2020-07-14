package com.mychelantonacio.packstar.view.activities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mychelantonacio.packstar.R;

import static com.mychelantonacio.packstar.view.activities.CreateBagActivity.NOTIFICATION_CHANNEL_ID;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id" ;
    public static String NOTIFICATION = "notification" ;


    @Override
    public void onReceive (Context context , Intent intent) {

        //if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE );
            Notification notification = intent.getParcelableExtra( NOTIFICATION );

            if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);
            }

            int id = intent.getIntExtra( NOTIFICATION_ID , 0 );
            assert notificationManager != null;
            notificationManager.notify(id , notification);
        //}

    }
}