package com.example.firebase_photo;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by lukepan on 2016/10/2.
 */

public class MyFirebaseINstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseINstanceIdSer";
    @Override
    public void onTokenRefresh() {
        //super.onTokenRefresh();  has been removed.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"token:"+token);
    }
}
