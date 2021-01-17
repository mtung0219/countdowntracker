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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CountdownTrackerWidgetRemoveViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext = null;
    private int appWidgetId;
    private Cursor mCursor;
    private List<String> widgetList;
    private EventViewModel mEventViewModel;
    private List<Event> currentEvents;
    private String doesthiswork;
    private String[] doesthisworkarray;

    private void updateWidgetListView() {
        widgetList = new ArrayList<String>();
        //currentEvents = (List<Event>) mEventViewModel.getAllEvents();
        this.widgetList.add("asdf");
        this.widgetList.add("asdf2");
        this.widgetList.add("asdf3");
    }
    public CountdownTrackerWidgetRemoveViewsFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        doesthiswork = intent.getStringExtra("doesthiswork");
        doesthisworkarray = intent.getStringArrayExtra("doesthisworkarray");
        Log.d("Loading", "app widget id is: " + String.valueOf(appWidgetId) + " and doesthiswork is: " + doesthiswork);
    }

    @Override
    public void onCreate() {
        updateWidgetListView();
    }

    @Override
    public void onDataSetChanged() {
        updateWidgetListView();
    }

    @Override
    public void onDestroy() {
        widgetList.clear();
    }

    @Override
    public int getCount() {
        return widgetList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(),
                R.layout.listview_item);

        Log.d("Loading", widgetList.get(position));
        Log.d("Loading", "test string is now: " + doesthiswork);
        if (doesthisworkarray != null) remoteView.setTextViewText(R.id.word_textview_listver, doesthisworkarray[position]);
        long now = new Date().getTime() / 1000;
        //remoteView.setTextViewText(R.id.daysleft_textview_listver, widgetList.get(position));
        remoteView.setTextViewText(R.id.daysleft_textview_listver, now+"");

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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
