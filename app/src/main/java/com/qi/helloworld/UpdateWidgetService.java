package com.qi.helloworld;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class UpdateWidgetService extends Service {


    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
