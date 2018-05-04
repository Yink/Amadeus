package com.example.yink.amadeus;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class AlarmService extends IntentService {

    private static final String CHANNEL_ID = "amadeus_channel_alarm";

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification(getString(R.string.incoming_call));
        Intent launch = new Intent(this, LaunchActivity.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launch);
    }

    private void sendNotification(String msg) {
        NotificationManager alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (alarmNotificationManager == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alarmNotificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, getString(R.string.pref_alarm), NotificationManager.IMPORTANCE_HIGH));
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LaunchActivity.class), 0);

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name)).setSmallIcon(R.drawable.incoming_call)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg)
                .setChannelId(CHANNEL_ID);

        alarmNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(Alarm.ALARM_NOTIFICATION_ID, alarmNotificationBuilder.build());
    }
}