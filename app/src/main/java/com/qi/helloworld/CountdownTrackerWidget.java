package com.qi.helloworld;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class CountdownTrackerWidget extends AppWidgetProvider {


    private String doesthiswork;
    private String[] doesthisworkarray;
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.d("Loading","widget update firing");
        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_tracker_widget);

        Intent intent = new Intent(context, CountdownTrackerWidgetRemoteViewsService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                appWidgetId);
        intent.putExtra("doesthiswork",doesthiswork);
        intent.putExtra("doesthisworkarray",doesthisworkarray);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        views.setRemoteAdapter(R.id.listview, intent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
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

            doesthiswork = getFromIntent;
            doesthisworkarray = intent.getStringArrayExtra("doesthisworkarray");
            Log.d("Loading","intermediate step is: " + doesthiswork);
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