package com.example.yink.amadeus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    private static Ringtone ringtone = null;

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO: Should we use ringtone or switch to MediaPlayer with loop?
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

    public static void stopRingtone() {
        if (ringtone != null) {
            if (ringtone.isPlaying()) {
                ringtone.stop();
            }
        }
    }

    public static boolean isPlaying() {
        if (ringtone != null) {
            return ringtone.isPlaying();
        }
        return false;
    }
}