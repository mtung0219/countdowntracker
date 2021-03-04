package com.qi.daysleftcountdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CountdownTrackerWidget extends AppWidgetProvider {
    private boolean first_sched_run2 = true;
    private String doesthiswork = "???";
    private String[] eventArray;
    private long[] datelongarray;
    Random r = new Random();
    int random = r.nextInt(999999);
    private static final String SHARED_PREFS_FILE = "SHARED_PREFS_FILE";
    private static final String LAST_UPDATED_COUNTDOWN_TRACKER = "LAST_UPDATED_COUNTDOWN_TRACKER";
    public static int UPDATE_MIN = 1;


    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, CountdownTrackerWidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);


        PendingIntent pendingI = PendingIntent.getActivity(
                context,
                0,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        intent.putExtra("doesthiswork",doesthiswork);
        intent.putExtra("doesthisworkarray",eventArray);
        intent.putExtra("dateLongArray",datelongarray);

        //intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        intent.setData(Uri.withAppendedPath(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)), String.valueOf(appWidgetId) + random));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_tracker_widget);
        views.setRemoteAdapter(R.id.listview, intent);
        views.setRemoteAdapter(R.id.widgetLastUpdated,intent);

        views.setOnClickPendingIntent(R.id.widgetButton, pendingI);

        Calendar now = Calendar.getInstance();
        if (eventArray != null && datelongarray != null) {
            views.setTextViewText(R.id.widgetLastUpdated,"Last updated " + now.getTime().toString());
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } else {
            views.setTextViewText(R.id.widgetLastUpdated,"Last updated " + now.getTime().toString());
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
        }

        // Instruct the widget manager to update the widget
        //appWidgetManager.updateAppWidget(appWidgetId, null);

        //context.startService(intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        first_sched_run2 = sharedPreferences.getBoolean("first_sched_run3", true);

        if(first_sched_run2) {
            scheduleNextUpdate(context, appWidgetIds);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("first_sched_run3", false);
            editor.apply();
        }
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        //Log.d("widget", "WIDGET ENABLED");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("first_sched_run3", true);
        editor.apply();

        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();

        //Log.d("widget", "widget receiving request");

        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
            Calendar now = Calendar.getInstance();
            Date dateNow = now.getTime();
            SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);

            long lastUpdated = prefs.getLong(LAST_UPDATED_COUNTDOWN_TRACKER,0);
            boolean update_stale = false;

            //Log.d("widget","last updated is ");
            //Log.d("widget", String.valueOf(lastUpdated));

            if ((dateNow.getTime() - lastUpdated) > (60000*UPDATE_MIN+1000))
                update_stale = true;

            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, CountdownTrackerWidget.class);


            if (((intent.getStringExtra("scheduledUpdate") != null &&
                    intent.getStringExtra("scheduledUpdate").equals("yes"))) || update_stale) {

                int[] appWidgetIds = mgr.getAppWidgetIds(cn);
                scheduleNextUpdate(context, appWidgetIds);
            }
            String getFromIntent = intent.getStringExtra("doesthiswork");
            String[] getArrayFromIntent = intent.getStringArrayExtra("doesthisworkarray");
            long[] getLongFromIntent = intent.getLongArrayExtra("dateLongArray");

            if (getFromIntent != null) {
                doesthiswork = getFromIntent;
            }

            if (getArrayFromIntent != null && getLongFromIntent != null) {
                eventArray = getArrayFromIntent;
                datelongarray = getLongFromIntent;
            } else {
                Gson gson = new Gson();
                String jsonText = prefs.getString("eventList", null);
                Event[] text = gson.fromJson(jsonText, Event[].class);
                List<Event> e = Arrays.asList(text);

                // get # of valid events first, since this is coming from SharedPreferences
                // reason for this is so events do not go to -1 on midnight
                List<String> testStringList = new ArrayList<>();
                List<Long> testLongList = new ArrayList<>();

                for (int i = 0; i < e.size(); i++) {
                    if (e.get(i).getDaysLeft() >= 0) {
                        testStringList.add(e.get(i).getName());
                        testLongList.add(e.get(i).getDateLong());
                    }
                }

                prefs.edit().putLong(LAST_UPDATED_COUNTDOWN_TRACKER, dateNow.getTime()).apply();

                // converting from arraylist to array (for parcelable purposes)
                String[] testStringArray = new String[testLongList.size()];
                long[] testLongArray = new long[testLongList.size()];

                for (int i = 0; i < testLongList.size(); i++) {
                    testStringArray[i] = testStringList.get(i);
                    testLongArray[i] = testLongList.get(i);
                }

                eventArray = testStringArray;
                datelongarray = testLongArray;
            }


            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.listview);
        }

        super.onReceive(context, intent);
    }

    private static void scheduleNextUpdate(Context context, int[] appWidgetIds) {
        // credit to Alexander Otavka on stack overflow for this method
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, CountdownTrackerWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra("scheduledUpdate","yes");
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        int millisInDay = 86400000;
        Calendar now = Calendar.getInstance();
        Date dateNow = now.getTime();

        // Get a calendar instance for midnight tomorrow.
        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        // Schedule one second after midnight, to be sure we are in the right day next time this
        // method is called.  Otherwise, we risk calling onUpdate multiple times within a few
        // milliseconds
        midnight.set(Calendar.SECOND, 2);
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.add(Calendar.DAY_OF_YEAR, 1);

        // For API 19 and later, set may fire the intent a little later to save battery,
        // setExact ensures the intent goes off exactly at midnight.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(),millisInDay, pendingIntent);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(),60000*UPDATE_MIN, pendingIntent);
        } else {
            // commented out version updates every minute
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateNow.getTime() + 60000*UPDATE_MIN, pendingIntent);
            //alarmManager.setExact(AlarmManager.RTC_WAKEUP, midnight.getTimeInMillis(), pendingIntent);
            //Log.d("widget", "alarm set for  " + (dateNow.getTime() + 60000*UPDATE_MIN));
        }
    }
}