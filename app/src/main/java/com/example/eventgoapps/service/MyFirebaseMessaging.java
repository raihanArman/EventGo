package com.example.eventgoapps.service;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.eventgoapps.data.remote.model.Event;
import com.example.eventgoapps.receiver.EventReceiver;
import com.example.eventgoapps.util.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData() != null){
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String idEvent = data.get("id_event");
            String tokenAdmin = data.get("token_admin");
            String message = data.get("message");
            Intent intent = new Intent(this, EventReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("token_admin", tokenAdmin);
            intent.putExtra("id_event", idEvent);
            intent.putExtra("message", message);
            intent.setAction(Utils.NOTIF_EVENT_USULAN);
            sendBroadcast(intent);
        }
    }
}
