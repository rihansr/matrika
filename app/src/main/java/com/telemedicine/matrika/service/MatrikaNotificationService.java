package com.telemedicine.matrika.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.telemedicine.matrika.BuildConfig;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.activity.DoctorHomeActivity;
import com.telemedicine.matrika.activity.PatientHomeActivity;
import com.telemedicine.matrika.helper.NotificationManager;
import com.telemedicine.matrika.model.chat.Group;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.enums.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MatrikaNotificationService extends FirebaseMessagingService {

    public static int               NOTIFICATION_ID = 1;
    private LocalBroadcastManager   tokenBroadcaster;

    @Override
    public void onCreate() {
        tokenBroadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.i(Constants.TAG, "New Token: " + token);
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        Intent tokenIntent = new Intent(Constants.TOKEN_LISTENER_KEY);
        tokenIntent.putExtra(Constants.TOKEN_INTENT_KEY, token);
        tokenBroadcaster.sendBroadcast(tokenIntent);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> getData = remoteMessage.getData();
        if(getData.containsKey(NotificationManager.DATA_KEY)){
            Map<String, String> extraData = new HashMap<>();
            extraData = (Map<String, String>) new Gson().fromJson(getData.get(NotificationManager.DATA_KEY), extraData.getClass());

            if (extraData != null) {
                if (extraData.containsKey("userLoggedIn") && FirebaseAuth.getInstance().getCurrentUser() == null) return;
                else if (extraData.containsKey("allowOnlyInBackground") && isAppRunInForeground(this)) return;
                else if (extraData.containsKey("messageSender") && extraData.containsKey("chatGroup")) {
                    Group group = new Gson().fromJson(extraData.get("chatGroup"), Group.class);
                    User sender = new Gson().fromJson(extraData.get("messageSender"), User.class);
                    String receiverId = extraData.get("messageReceiverId");
                    if (receiverId == null || !receiverId.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())) return;
                    if (sender != null && group != null) {
                        Intent intent = new Intent(getBaseContext(), sender.getRole().equals(Role.PATIENT.getId()) ? DoctorHomeActivity.class : PatientHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra(Constants.CHAT_GROUP_BUNDLE_KEY, group);
                        intent.putExtra(Constants.USER_BUNDLE_KEY, sender);
                        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                        showNotification(getData.get(NotificationManager.TITLE_KEY), getData.get(NotificationManager.MESSAGE_KEY), contentIntent);
                        return;
                    }
                }
            }
        }

        showNotification(getData.get(NotificationManager.TITLE_KEY), getData.get(NotificationManager.MESSAGE_KEY), null);
    }

    private void showNotification(String title, String message, PendingIntent contentIntent) {
        String CHANNEL_ID = BuildConfig.APPLICATION_ID;
        String CHANNEL_NAME = "Matrica";

        android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setAutoCancel(true)
                .setVibrate(new long[] { 100, 100, 100, 100, 100 })
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.DEFAULT_LIGHTS);

        if(contentIntent != null) builder.setContentIntent(contentIntent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, android.app.NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(message);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.CYAN);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            assert notificationManager != null;
            builder.setChannelId(CHANNEL_ID);
            notificationManager.createNotificationChannel(channel);
        }

        if (NOTIFICATION_ID > 1073741824) NOTIFICATION_ID = 0;

        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID++,builder.build());
    }

    public static boolean isAppRunInForeground(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : l) {
            if (info.uid == context.getApplicationInfo().uid && info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }
}
