package com.example.eventgoapps.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class NearbyEventService extends Service {

    private static final String TAG = "NearbyEventService";

    public NearbyEventService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new LoadEventTerdekat(getApplicationContext()).mHandler.sendEmptyMessage(1);
        Log.d(TAG, "onStartCommand: Service aktif");
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Service hancur");
    }
}
