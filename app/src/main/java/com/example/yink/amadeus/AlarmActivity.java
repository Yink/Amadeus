package com.example.yink.amadeus;

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
    Intent alarmIntent;

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
        alarmIntent = new Intent(this, AlarmReceiver.class);
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
            Log.d(TAG, "Alarm On");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            if (pendingIntent != null) {
                editor.putBoolean("alarm_toggle", false);
                AlarmReceiver.stopRingtone();
                alarmManager.cancel(pendingIntent);
                Log.d(TAG, "Alarm Off");
            }
        }
        editor.apply();
    }
}