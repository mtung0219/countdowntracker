package com.qi.daysleftcountdown;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class CountdownTrackerWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d("Loading","PASSING THROUGH SERVICE");
        return new CountdownTrackerWidgetRemoveViewsFactory(this.getApplicationContext(), intent);
    }
}
