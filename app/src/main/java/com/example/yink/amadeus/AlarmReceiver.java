package com.example.yink.amadeus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    static MediaPlayer m;

    @Override
    public void onReceive(Context context, Intent intent) {
        int[] ringtones = {
                R.raw.ringtone_gate_of_steiner, R.raw.ringtone_village,
                R.raw.ringtone_beginning_of_fight, R.raw.ringtone_easygoingness,
                R.raw.ringtone_reunion, R.raw.ringtone_precaution,
                R.raw.ringtone_over_the_sky
        };

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        int index = Integer.parseInt(settings.getString("ringtone", "0"));

        m = MediaPlayer.create(context, ringtones[index]);

        m.setLooping(true);
        m.start();

        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

    public static void stopRingtone() {
        if (m != null) {
            if (m.isPlaying()) {
                m.stop();
            }
        }
    }

    public static boolean isPlaying() {
        if (m != null) {
            return m.isPlaying();
        }
        return false;
    }
}