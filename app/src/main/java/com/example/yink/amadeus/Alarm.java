package com.example.yink.amadeus;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;

class Alarm {

    private static MediaPlayer m;
    private static SharedPreferences settings;
    private static Vibrator v;

    static final int ALARM_ID = 104859;
    static final int ALARM_NOTIFICATION_ID = 102434;

    private static final String TAG = "Alarm";
    private static boolean isPlaying = false;
    private static PowerManager.WakeLock sCpuWakeLock;

    static void start(Context context, int ringtone) {

        acquireCpuWakeLock(context);

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

        Log.d(TAG, "Start");

    }

    static void cancel(Context context) {

        settings = PreferenceManager.getDefaultSharedPreferences(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (isPlaying) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("alarm_toggle", false);
            editor.apply();
            m.release();
            notificationManager.cancel(ALARM_NOTIFICATION_ID);
            alarmManager.cancel(pendingIntent);
            releaseCpuLock();
            isPlaying = false;
            if (v != null) {
                v.cancel();
            }
        }

        Log.d(TAG, "Cancel");

    }

    static boolean isPlaying() {
        return isPlaying;
    }

    private static void acquireCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }

        PowerManager pm =
                (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        sCpuWakeLock = pm.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE, TAG);
        sCpuWakeLock.acquire();
    }

    private static void releaseCpuLock() {
        if (sCpuWakeLock != null) {
            sCpuWakeLock.release();
            sCpuWakeLock = null;
        }
    }

}
