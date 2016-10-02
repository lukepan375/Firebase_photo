package com.example.firebase_photo;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by lukepan on 2016/10/2.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingServ";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        Log.d(TAG,"onMessageReceived,id:"+remoteMessage.getMessageId());
        Log.d(TAG,"onMessageReceived,messageType:"+remoteMessage.getMessageType());

    }
}
