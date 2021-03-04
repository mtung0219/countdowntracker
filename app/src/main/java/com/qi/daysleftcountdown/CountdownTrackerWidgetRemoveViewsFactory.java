package com.qi.daysleftcountdown;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.Calendar;
import java.util.Date;

public class CountdownTrackerWidgetRemoveViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private int appWidgetId;
    private String doesthiswork;
    private String[] eventArray;
    private long[] datelongarray;
    private Date dateNow;
    private SharedPreferences sp;
    private int currentDisplay;


    public CountdownTrackerWidgetRemoveViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        sp = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        this.currentDisplay = sp.getInt(SettingsActivity.PREFERENCE_DISPLAY_CODE,0);
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        doesthiswork = intent.getStringExtra("doesthiswork");
        eventArray = intent.getStringArrayExtra("doesthisworkarray");
        datelongarray = intent.getLongArrayExtra("dateLongArray");
        dateNow = getNow();
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        if (eventArray != null) return eventArray.length;
        else return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        //Log.d("widget", "updating widget view");

        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
                R.layout.listview_item);

        if (eventArray != null)
            remoteView.setTextViewText(R.id.word_textview_listver, eventArray[position]);

        String daysLeft="";
        Date d = new Date( datelongarray[position]);
        int daysLeftInt = getDaysLeft(d);
        int[] ymd = getYearsMonthsDaysLeft(d);


        if (daysLeftInt ==0 ) {daysLeft = "today";}
        else if (SettingsActivity.displayModes[currentDisplay].equals("Year/Month/Day")) {
            if (ymd[0] > 0) daysLeft += ymd[0] + "y ";
            if (ymd[1] > 0) {
                int months = (int) (ymd[1] / 30.5);
                if (months > 0 || ymd[0] > 0) daysLeft += months + "mo ";
                int days = (int) (ymd[1] % 30.5);
                daysLeft += days + "d";
                //daysLeft += ymd[1] + "d";
            }
        } else {
            daysLeft = daysLeftInt + "";
        }

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

    public int[] getYearsMonthsDaysLeft(Date d) {
        Calendar now = Calendar.getInstance();
        Calendar later = Calendar.getInstance();
        //now.setTimeInMillis(getNowInMillis());
        now.setTime(dateNow);
        later.setTime(d);
        return timeBetween(now, later);
    }


    private static int daysBetween(Calendar now, Calendar later){
        // credit to John Leehey from stackOverflow
        Calendar dayOne = (Calendar) now.clone(),
                dayTwo = (Calendar) later.clone();
        boolean flipResult;

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

    private static int[] timeBetween(Calendar now, Calendar later) {
        Calendar dayOne = (Calendar) now.clone(),
                dayTwo = (Calendar) later.clone();

        boolean isSwapped = false; // isSwapped false means that it is a past event
        if (dayTwo.getTime().getTime() > dayOne.getTime().getTime()) {
            //swap them, dayOne is always the later one
            Calendar temp = dayOne;
            dayOne = dayTwo;
            dayTwo = temp;
            isSwapped = true;
        }
        int yearsBetween = 0;
        int daysBetweenYear = 0;

        while (dayOne.getTime().getTime() > dayTwo.getTime().getTime()) {
            yearsBetween += 1;
            dayOne.add(Calendar.YEAR, -1);
        }

        if (dayOne.getTime().getTime() < dayTwo.getTime().getTime()) {
            yearsBetween -= 1;
            dayOne.add(Calendar.YEAR, 1);
        }

        while (dayOne.getTime().getTime() > dayTwo.getTime().getTime()) {
            daysBetweenYear += 1;
            dayOne.add(Calendar.SECOND, -1 * 60 * 60 * 24);
        }

        if (dayOne.getTime().getTime() < dayTwo.getTime().getTime() && isSwapped) {
            daysBetweenYear -= 1;
            dayOne.add(Calendar.SECOND, 1 * 60 * 60 * 24);
        }

        return new int[]{yearsBetween, daysBetweenYear};

    }

}
