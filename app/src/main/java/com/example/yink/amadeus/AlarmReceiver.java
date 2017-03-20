package com.example.yink.amadeus;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends WakefulBroadcastReceiver {

    static MediaPlayer m;
    static boolean isPlaying = false;
    static SharedPreferences settings;
    static SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        int[] ringtones = {
                R.raw.ringtone_gate_of_steiner, R.raw.ringtone_village,
                R.raw.ringtone_beginning_of_fight, R.raw.ringtone_easygoingness,
                R.raw.ringtone_reunion, R.raw.ringtone_precaution,
                R.raw.ringtone_over_the_sky
        };

        settings = PreferenceManager.getDefaultSharedPreferences(context);
        int index = Integer.parseInt(settings.getString("ringtone", "0"));

        m = MediaPlayer.create(context, ringtones[index]);

        m.setLooping(true);
        m.start();

        if (m.isPlaying()) {
            isPlaying = true;
        }

        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

    public static void stopRingtone(Context context) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (isPlaying) {
            editor = settings.edit();
            editor.putBoolean("alarm_toggle", false);
            editor.apply();
            m.release();
            isPlaying = false;
        }
    }

    public static boolean isPlaying() {
        return isPlaying;
    }
}