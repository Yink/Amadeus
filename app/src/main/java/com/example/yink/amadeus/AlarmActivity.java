package com.example.yink.amadeus;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {

    private final String TAG = "AlarmActivity";

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private TimePicker alarmTimePicker;
    private ToggleButton alarmToggle;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmToggle = (ToggleButton) findViewById(R.id.alarmToggle);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(this, Alarm.ALARM_ID, new Intent(this, AlarmReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);

        alarmTimePicker.setIs24HourView(settings.getBoolean("24-hour_format", true));

        if (settings.getBoolean("alarm_toggle", false)) {
            alarmToggle.setChecked(true);
        } else {
            alarmToggle.setChecked(false);
        }
    }

    public void onToggleClicked(View view) {
        SharedPreferences.Editor editor = settings.edit();

        if (alarmToggle.isChecked()) {
            editor.putBoolean("alarm_toggle", true);
            Calendar calendar = Calendar.getInstance();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "Current API functions have been executed");
                setTime(calendar, editor);
            } else {
                Log.d(TAG, "Legacy API functions have been executed");
                setTimeLegacy(calendar, editor);
            }

            Log.d(TAG, "Alarm On");
        } else {
            Alarm.cancel(this);

            editor.putBoolean("alarm_toggle", false);
            Log.d(TAG, "Alarm Off");
        }
        editor.apply();
    }

    @SuppressWarnings("deprecation")
    private void setTimeLegacy(Calendar calendar, SharedPreferences.Editor editor) {
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
        editor.putLong("alarm_time", calendar.getTimeInMillis());
        Toast.makeText(this, "Alarm has been set for " + alarmTimePicker.getCurrentHour() + " hour(s) " + alarmTimePicker.getCurrentMinute() + " minute(s)", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setTime(Calendar calendar, SharedPreferences.Editor editor) {
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getMinute());

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        editor.putLong("alarm_time", calendar.getTimeInMillis());
        Toast.makeText(this, "Alarm has been set for " + alarmTimePicker.getHour() + " hour(s) " + alarmTimePicker.getMinute() + " minute(s)", Toast.LENGTH_SHORT).show();
    }
}