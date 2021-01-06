package com.ioannisnicos.ethiomoviesuser.notification;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ioannisnicos.ethiomoviesuser.R;

public class NotfApplication extends Application {

    private static final String TAG = NotfApplication.class.getSimpleName();

    MyAppsNotificationManager  myAppsNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        myAppsNotificationManager = MyAppsNotificationManager.getInstance(this);

        myAppsNotificationManager.registerNotificationChannelChannel(
                getString(R.string.MEDIA_CHANNEL_ID),
                getString(R.string.CHANNEL_MEDIA_NAME),
                getString(R.string.MEDIA_CHANNEL_DESCRIPTION));

        myAppsNotificationManager.registerNotificationChannelChannel(
                getString(R.string.ACCOUNT_CHANNEL_ID),
                getString(R.string.CHANNEL_ACCOUNT_NAME),
                getString(R.string.ACCOUNT_CHANNEL_DESCRIPTION));


        FirebaseMessaging.getInstance().isAutoInitEnabled();

//        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//            @Override
//            public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                if(!task.isSuccessful()){
//                    Log.i("R.string.DEBUG_TAG"," Task Filed");
//                    return;
//                }
//
//                //Log.i("R.string.DEBUG_TAG"," The completed result: "+task.getResult().getToken());
//                //Making an API call - Thread, Volley, okHttp, Retrofit
//            }
//        });

    }


    public void triggerNotificationWithBackStackImageSubscription(Class targetNotificationActivity, String channelId,
                                                                  String title, String body, String status,
                                                                  String largeImg, int priority,
                                                                  boolean autoCancel, int notificationId,
                                                                  int pendingIntentFlag){
        myAppsNotificationManager.triggerNotificationWithBackStackImageSubscription(targetNotificationActivity,channelId,title
                                                                                    ,body, status,largeImg, priority,
                                                                                     autoCancel,notificationId, pendingIntentFlag);
    }


    public void triggerNotificationWithBackStackImageRent(Class targetNotificationActivity, String channelId,
                                                          String title, String body, String bigText,
                                                          String largeImg, String BigImg, int priority,
                                                          boolean autoCancel, int notificationId,
                                                          int pendingIntentFlag){


        myAppsNotificationManager.triggerNotificationWithBackStackImageRent(targetNotificationActivity,channelId,title
                                                                            ,body, bigText,largeImg,BigImg, priority,
                                                                            autoCancel,notificationId, pendingIntentFlag);
    }



    public void cancelNotification(int notificaitonId){
        myAppsNotificationManager.cancelNotification(notificaitonId);
    }





}
