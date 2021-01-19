package com.qi.helloworld;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Ignore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CountdownTrackerWidgetRemoveViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext = null;
    private int appWidgetId;
    private List<String> widgetList;
    private String doesthiswork;
    private String[] doesthisworkarray;
    private long[] datelongarray;
    private Date dateNow;

    private void updateWidgetListView() {
        widgetList = new ArrayList<String>();
        this.widgetList.add("asdf");
        this.widgetList.add("asdf2");
        this.widgetList.add("asdf3");
    }
    public CountdownTrackerWidgetRemoveViewsFactory(Context applicationContext, Intent intent) {
        Log.d("Loading","REMOTE VIEWS FACTORY INTENTS LOOKED AT");
        mContext = applicationContext;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        doesthiswork = intent.getStringExtra("doesthiswork");
        doesthisworkarray = intent.getStringArrayExtra("doesthisworkarray");
        datelongarray = intent.getLongArrayExtra("dateLongArray");
        dateNow = getNow();
    }

    @Override
    public void onCreate() { updateWidgetListView(); }

    @Override
    public void onDataSetChanged() {
        Log.d("Loading","ON DATA SET CHANGE CALLED");
        updateWidgetListView();

        //EventRoomDatabase db = EventRoomDatabase.getDatabase(mContext.getApplicationContext());
        //EventDao mEventDao = db.eventDao();
        //LiveData<List<Event>> mCurrentEvents = mEventDao.getCurrentEvents();
        //EventsFromRoom = mCurrentEvents.getValue();
        //Log.d("Loading", "string value of db is " + String.valueOf(EventsFromRoom));
    }

    @Override
    public void onDestroy() {
        widgetList.clear();
    }

    @Override
    public int getCount() {
        if (doesthisworkarray != null) return doesthisworkarray.length;
        else return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
                R.layout.listview_item);

        Log.d("Loading", "test string is now: " + doesthiswork);
        if (doesthisworkarray != null) remoteView.setTextViewText(R.id.word_textview_listver, doesthisworkarray[position]);

        String daysLeft;
        Date d = new Date( datelongarray[position]);
        if (getDaysLeft(d) == 0) { daysLeft = "today!"; }
        else { daysLeft = getDaysLeft(d) + ""; }

        if (datelongarray != null) {
            remoteView.setTextViewText(R.id.daysleft_textview_listver, daysLeft);
        }

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public boolean hasStableIds() { return false; }

    public int getDaysLeft(Date d) {
        Calendar now = Calendar.getInstance();
        Calendar later = Calendar.getInstance();
        //now.setTimeInMillis(getNowInMillis());
        now.setTime(dateNow);
        later.setTime(d);
        return daysBetween(now, later);
    }

    public Date getNow() {
        Calendar c = Calendar.getInstance();
        Date prelimDate = c.getTime();

        c.setTime(prelimDate);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        return c.getTime();
    }

    @Ignore
    private static int daysBetween(Calendar now, Calendar later){
        // credit to John Leehey from stackOverflow
        Calendar dayOne = (Calendar) now.clone(),
                dayTwo = (Calendar) later.clone();
        boolean flipResult;
        Log.d("ASDF2","finding days between " + dayOne.getTime().getTime() + " and " +  dayTwo.getTime().getTime());

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            //return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
            return dayTwo.get(Calendar.DAY_OF_YEAR) - dayOne.get(Calendar.DAY_OF_YEAR);
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
                flipResult = false;
            } else {
                flipResult = true;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }
            if (flipResult) return -(extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays);
            else return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays ;
        }
    }
}
