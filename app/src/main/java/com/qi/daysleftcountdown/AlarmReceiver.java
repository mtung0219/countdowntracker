package com.qi.daysleftcountdown;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;


public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    private String eventName;

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("updateWidget")) {
            ComponentName thisWidget = new ComponentName(context, CountdownTrackerWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_tracker_widget);
            manager.updateAppWidget(thisWidget, views);

            return;
        }

        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        eventName = intent.getStringExtra("eventName");
        String notificationType = intent.getStringExtra("type");
        int notifyId = Integer.parseInt(intent.getAction());
        deliverNotification(context, notifyId, notificationType);
    }

    private void deliverNotification(Context context, int notifyId, String notificationType) {
        String contentText="You have an event coming up!";
        if (notificationType.equals("dayOf")) {
            contentText = eventName + " is today!";
        } else if (notificationType.equals("oneDay")) {
            contentText = eventName + " coming up in one day!";
        }
        Intent contentIntent = new Intent(context, MainActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (context, notifyId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Countdown Tracker")
                .setContentText(contentText)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        mNotificationManager.notify(notifyId, builder.build());
    }

}