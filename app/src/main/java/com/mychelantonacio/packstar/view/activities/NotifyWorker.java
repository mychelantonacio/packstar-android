package com.mychelantonacio.packstar.view.activities;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mychelantonacio.packstar.R;

import static com.mychelantonacio.packstar.view.activities.CreateBagActivity.NOTIFICATION_CHANNEL_ID;
import static com.mychelantonacio.packstar.view.activities.ReminderBroadcastReceiver.NOTIFICATION_ID;


public class NotifyWorker extends Worker {

    private long delay;
    private Context context;
    
    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);

        Data data = params.getInputData();
        this.delay =  data.getLong("delay", 10);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        // Method to trigger an instant notification
        triggerNotification(delay);





        return Result.success();
        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }


    public void triggerNotification(long finalDelay){

        final String NOTIFICATION_CHANNEL_ID = "10001";
        final String default_notification_channel_id = "default";



        Intent intent = new Intent(context, ListBagActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, default_notification_channel_id)
                .setSmallIcon(R.drawable.splashscreen_image)
                .setContentTitle("Packstar Notification")
                .setContentText("jojoba")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setChannelId( NOTIFICATION_CHANNEL_ID )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE );
        Notification notification = builder.build();

        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "PackStar Notification Center" , importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        int id = intent.getIntExtra( NOTIFICATION_ID , 0 );
        assert notificationManager != null;
        notificationManager.notify(id , notification);


    }

}
