package com.example.eventgoapps.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.eventgoapps.R;
import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.ui.DetailEventActivity;
import com.example.eventgoapps.util.Utils;

public class NearbyReceiver extends BroadcastReceiver {

    Event event;
    private static final String TAG = "NearbyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire();
        String action = intent.getAction();
        if (action.equals(Utils.NOTIF_EVENT_NEARBY)){
            event = intent.getParcelableExtra("data_event");
            sendNotifcation(context, context.getResources().getString(R.string.app_name), "Event terdekat pada lokasi anda : "+
                    event.getJudul(), event.getIdEvent());
        }
    }

    private void sendNotifcation(Context context, String title, String desc, String idEvent) {
        Log.d(TAG, "sendNotifcation: ");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, DetailEventActivity.class);
        intent.putExtra("id_event", idEvent);
        PendingIntent pendingIntent =PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIF_CHANNEL_ID = "event_channel_id";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int important =NotificationManager.IMPORTANCE_HIGH;
            String NOTIFICATION_CHANNEL_NAME = "event channel";
            NotificationChannel channel = new NotificationChannel(
                    NOTIF_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, important
            );
            notificationManager.createNotificationChannel(channel);
        }

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, desc)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(desc)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(uri)
                .setAutoCancel(true);

        int NOTIF_ID = 998;
        notificationManager.notify(NOTIF_ID, builder.build());

    }
}
