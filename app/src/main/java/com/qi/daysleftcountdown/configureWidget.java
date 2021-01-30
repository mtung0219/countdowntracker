package com.qi.daysleftcountdown;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

/**
 * Activity to configure widget upon initial creation.
 * Retrieves relevant event info and finishes.
 */
public class configureWidget extends AppCompatActivity {

    private static final String SHARED_PREFS_FILE = "SHARED_PREFS_FILE";
    private List<Event> finalEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventViewModel mEventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            // use this part of activity to configure the widget
            int appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) { finish(); }

            mEventViewModel.getCurrentEvents().observe(this, new Observer<List<Event>>() {
                @Override
                public void onChanged(@Nullable final List<Event> events) {
                    finalEvents = events;

                    // save the task list to preference
                    SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();

                    Gson gson = new Gson();
                    String jsonText = gson.toJson(finalEvents);
                    editor.putString("eventList", jsonText);
                    editor.apply();

                    Context ctx = getApplicationContext();
                    sendRefreshBroadcast(ctx);

                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            });
        }
    }

    public void sendRefreshBroadcast(Context context) {
        String[] eventArray = new String[finalEvents.size()];
        long[] dateArray = new long[finalEvents.size()];
        for (int i = 0; i < finalEvents.size(); i++) {
            eventArray[i]=(finalEvents.get(i).getName());
            dateArray[i] = (finalEvents.get(i).getDateLong());
        }
        String teststring = finalEvents.get(0).getName();

        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(context, CountdownTrackerWidget.class));

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, CountdownTrackerWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra("doesthiswork",teststring);
        intent.putExtra("doesthisworkarray",eventArray);
        intent.putExtra("dateLongArray",dateArray);
        context.sendBroadcast(intent);
    }
}