package com.example.yink.amadeus;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class AlarmActivity extends Activity {

    final String TAG = "Amadeus.Alarm";
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    TimePicker alarmTimePicker;
    ToggleButton alarmToggle;
    public static final int alarmCode = 104856;
    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, alarmCode, alarmIntent, PendingIntent.FLAG_NO_CREATE);

        if (settings.getBoolean("alarm_toggle", false)) {
            alarmToggle.setChecked(true);
        } else {
            alarmToggle.setChecked(false);
        }
    }

    public void onToggleClicked(View view) {
        editor = settings.edit();
        if (alarmToggle.isChecked()) {
            editor.putBoolean("alarm_toggle", true);
            Calendar calendar = Calendar.getInstance();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "Current API functions have been executed");
                calendar.set(Calendar.HOUR_OF_DAY, getCurrentHour(alarmTimePicker));
                calendar.set(Calendar.MINUTE, getCurrentMinute(alarmTimePicker));
                Toast.makeText(this, "Alarm has been set for " + getCurrentHour(alarmTimePicker) + " hour(s) " + getCurrentMinute(alarmTimePicker) + " minute(s)", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Legacy API functions have been executed");
                calendar.set(Calendar.HOUR_OF_DAY, getCurrentHourLegacy(alarmTimePicker));
                calendar.set(Calendar.MINUTE, getCurrentMinuteLegacy(alarmTimePicker));
                Toast.makeText(this, "Alarm has been set for " + getCurrentHourLegacy(alarmTimePicker) + " hour(s) " + getCurrentMinuteLegacy(alarmTimePicker) + " minute(s)", Toast.LENGTH_SHORT).show();
            }
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d(TAG, "Alarm On");
        } else {
            AlarmReceiver.stopRingtone();
            editor.putBoolean("alarm_toggle", false);
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Alarm Off");
        }
        editor.apply();
    }

    @SuppressWarnings("deprecation")
    public int getCurrentHourLegacy(TimePicker tp) {
        return tp.getCurrentHour();
    }

    @SuppressWarnings("deprecation")
    public int getCurrentMinuteLegacy(TimePicker tp) {
        return tp.getCurrentMinute();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public int getCurrentHour(TimePicker tp) {
        return tp.getHour();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public int getCurrentMinute(TimePicker tp) {
        return tp.getMinute();
    }
}