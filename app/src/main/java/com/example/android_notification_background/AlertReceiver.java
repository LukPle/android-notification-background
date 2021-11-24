package com.example.android_notification_background;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import static com.example.android_notification_background.MainActivity.EXTRA_CONTENT;

/**
 * The AlertReceiver shows a Notification to the user.
 *
 * In Android Manifest: receiver android:name=".AlertReceiver"
 */
public class AlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String contentText = intent.getStringExtra(EXTRA_CONTENT);

        Notification notification = new NotificationCompat.Builder(context, "Remind")
                .setSmallIcon(R.drawable.ic_calendar)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        managerCompat.notify(1, notification);
    }
}
