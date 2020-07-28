package com.example.eventgoapps.util;

import android.content.Context;
import android.graphics.Rect;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.View;
import android.widget.ScrollView;

public class Utils {

    public static final String LOGIN_KEY = "login_key";
    public static final String ID_USER_KEY = "id_user";
    public static final String LOGIN_STATUS = "status_login";
    public static final String NOTIF_TERDEKAT_STATUS = "status_notif_terdekat";
    public static final String EVENT_LOCATION = "event_location";
    public static final String DATA_EVENT = "Data Event";
    public static final String NOTIF_EVENT_NEARBY = "notif_event_terdekat";
    public static Location mLastLocation = null;
    public static final String mapsUrl = "https://maps.googleapis.com";
    public static final int TYPE_ADD = 82;
    public static final int TYPE_EDIT = 81;
    public static final int TYPE_INTENT_RIWAYAT = 10;
    public static final int TYPE_INTENT_AKTIVITAS = 11;

    public static boolean isConnectionInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null){
                for (int i=0; i<info.length; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void scrollToView(final View scrollView, final View view) {
        view.requestFocus();
        final Rect scrollBounds = new Rect();
        scrollView.getHitRect(scrollBounds);
        if (!view.getLocalVisibleRect(scrollBounds)) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    int toScroll = getRelativeTop(view) - getRelativeTop(scrollView);
                    ((ScrollView) scrollView).smoothScrollTo(0, toScroll-120);
                }
            });
        }
    }
    public static int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView()) return myView.getTop();
        else return myView.getTop() + getRelativeTop((View) myView.getParent());
    }
}
