package com.qi.helloworld;

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

import java.util.Calendar;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private String eventName;
    private String notificationType;
    private static final String PREFERENCE_LAST_NOTIF_ID_1 = "PREFERENCE_LAST_NOTIF_ID_1";

    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("notif","alarm receiver called");
        if (intent.getAction().equals("updateWidget")) {
            ComponentName thisWidget = new ComponentName(context, CountdownTrackerWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_tracker_widget);
            manager.updateAppWidget(thisWidget, views);

            return;
        }

        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("notif","getting this extra name : " + eventName);
        eventName = intent.getStringExtra("eventName");
        notificationType = intent.getStringExtra("type");
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
                //.setWhen(getStartLocalDateLong())
                .setContentTitle("Countdown Tracker")
                .setContentText(contentText)
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        mNotificationManager.notify(notifyId, builder.build());
    }

    private long getStartLocalDateLong() {
        Calendar c = Calendar.getInstance();
        Date prelimDate = c.getTime();

        c.setTime(prelimDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY,1);
        c.set(Calendar.MINUTE, 43);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime().getTime();
    }

}