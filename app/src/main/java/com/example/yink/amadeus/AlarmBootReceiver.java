package com.example.yink.amadeus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class AlarmBootReceiver extends WakefulBroadcastReceiver {

    final String TAG = "AlarmBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())
                && settings.getBoolean("alarm_toggle", false)) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(
                    Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    Alarm.ALARM_ID, new Intent(context, AlarmReceiver.class), 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, settings.getLong("alarm_time", 0), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, settings.getLong("alarm_time", 0), pendingIntent);
            }
            Log.d(TAG, "Alarm has been recovered");
        }

    }
}
