package com.qi.helloworld;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class CountdownTrackerWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Loading","passing through service");
        return new CountdownTrackerWidgetRemoveViewsFactory(this.getApplicationContext(), intent);
    }
}
