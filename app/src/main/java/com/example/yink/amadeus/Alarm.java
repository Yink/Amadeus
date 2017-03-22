package com.example.yink.amadeus;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.preference.PreferenceManager;

class Alarm {

    private static MediaPlayer m;
    private static SharedPreferences settings;
    private static Vibrator v;

    static final int ALARM_ID = 104859;
    static final int ALARM_NOTIFICATION_ID = 102434;

    private static boolean isPlaying = false;

    static void start(Context context, int ringtone) {

        settings = PreferenceManager.getDefaultSharedPreferences(context);

        if (settings.getBoolean("vibrate", false)) {
            v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {500, 2000};
            v.vibrate(pattern, 0);
        }

        m = MediaPlayer.create(context, ringtone);

        m.setLooping(true);
        m.start();

        if (m.isPlaying()) {
            isPlaying = true;
        }

    }

    static void cancel(Context context) {

        settings = PreferenceManager.getDefaultSharedPreferences(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (isPlaying) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("alarm_toggle", false);
            editor.apply();
            m.release();
            isPlaying = false;
        }

        notificationManager.cancel(ALARM_NOTIFICATION_ID);
        alarmManager.cancel(pendingIntent);
        //if (settings.getBoolean("vibrate", false)) {
        if (v != null) {
            v.cancel();
        }

    }

    static boolean isPlaying() {
        return isPlaying;
    }

}
