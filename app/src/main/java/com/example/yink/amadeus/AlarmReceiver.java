package com.example.yink.amadeus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Broadcast received!");

        int[] ringtones = {
                R.raw.ringtone_gate_of_steiner, R.raw.ringtone_village,
                R.raw.ringtone_beginning_of_fight, R.raw.ringtone_easygoingness,
                R.raw.ringtone_reunion, R.raw.ringtone_precaution,
                R.raw.ringtone_over_the_sky
        };

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        int index = Integer.parseInt(settings.getString("ringtone", "0"));

        Log.d(TAG, "Starting alarm...");

        Alarm.start(context, ringtones[index]);

        Intent service = new Intent(context, AlarmService.class);
        context.startService(service);
        setResultCode(Activity.RESULT_OK);
    }

}