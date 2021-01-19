package com.qi.helloworld;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Implementation of App Widget functionality.
 */
public class CountdownTrackerWidget extends AppWidgetProvider {

    public static String WIDGET_BUTTON = "com.qi.helloworld.WIDGET_BUTTON";
    private String doesthiswork = "???";
    private String[] doesthisworkarray;
    private long[] datelongarray;
    Random r = new Random();
    int random = r.nextInt(999999);
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.d("Loading","widget update firing");
        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object

        Intent intent = new Intent(context, CountdownTrackerWidgetRemoteViewsService.class);
        Intent intentButton = new Intent(WIDGET_BUTTON);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        PendingIntent pendingI = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingButton = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        intent.putExtra("doesthiswork",doesthiswork);

        intent.putExtra("doesthisworkarray",doesthisworkarray);
        intent.putExtra("dateLongArray",datelongarray);
        //intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        intent.setData(Uri.withAppendedPath(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)), String.valueOf(appWidgetId) + random));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_tracker_widget);
        views.setRemoteAdapter(R.id.listview, intent);
        views.setRemoteAdapter(R.id.widgetLastUpdated,intent);
        views.setOnClickPendingIntent(R.id.widgetButton, pendingI);


        if (doesthisworkarray != null && datelongarray != null) {
            Calendar now = Calendar.getInstance();

            views.setTextViewText(R.id.widgetLastUpdated,"Last updated " + now.getTime().toString());
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, null);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        context.startService(intent);
    }

    private String dateToString(Calendar c) {
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        int h = c.get(Calendar.HOUR);
        int z = c.get(Calendar.MINUTE);
        return m + "/" + d + "/" + y + " " + h + ":" + z;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d("Loading","widget enabled firing");

        super.onEnabled(context);
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("Loading","widget on receive firing");
        final String action = intent.getAction();

        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
            String getFromIntent = intent.getStringExtra("doesthiswork");
            String[] getArrayFromIntent = intent.getStringArrayExtra("doesthisworkarray");
            long[] getLongFromIntent = intent.getLongArrayExtra("dateLongArray");


            if (getFromIntent != null) {
                doesthiswork = getFromIntent;
            }
            if (getArrayFromIntent != null) { doesthisworkarray = getArrayFromIntent; }
            if (getLongFromIntent != null) { datelongarray = getLongFromIntent; }

            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, CountdownTrackerWidget.class);

            //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_tracker_widget);
            //Intent intent1 = new Intent(context, CountdownTrackerWidgetRemoteViewsService.class);
            //intent1.putExtra("doesthiswork",doesthiswork);
            //.setData(Uri.parse(intent1.toUri(Intent.URI_INTENT_SCHEME)));

            //views.setRemoteAdapter(R.id.listview, intent1);

            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.listview);
        }
        super.onReceive(context, intent);
    }
}