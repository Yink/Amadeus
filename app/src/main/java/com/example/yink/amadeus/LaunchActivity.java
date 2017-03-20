package com.example.yink.amadeus;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LaunchActivity extends AppCompatActivity {
    ImageView connect, cancel, imageViewLogo;
    TextView status;
    Boolean isPressed = false;
    SharedPreferences settings;
    MediaPlayer m;
    Handler aniHandle = new Handler();
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    NotificationManager notificationManager;

    int i = 0;
    int id;
    int duration = 20;
    Runnable aniRunnable = new Runnable() {
        public void run() {
            if (i < 39) {
                i += 1;
                String imgName = "logo" + Integer.toString(i);
                id = getResources().getIdentifier(imgName, "drawable", getPackageName());
                imageViewLogo.setImageDrawable((ContextCompat.getDrawable(getApplicationContext(), id)));
                aniHandle.postDelayed(this, duration);
            }
        }
    };

    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        connect = (ImageView) findViewById(R.id.imageView_connect);
        cancel = (ImageView) findViewById(R.id.imageView_cancel);
        status = (TextView) findViewById(R.id.textView_call);
        imageViewLogo = (ImageView) findViewById(R.id.imageView_logo);
        aniHandle.post(aniRunnable);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, AlarmActivity.alarmCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!isAppInstalled(LaunchActivity.this, "com.google.android.googlequicksearchbox")) {
            status.setText(R.string.google_app_error);
        }
        if (AlarmReceiver.isPlaying()) {
            status.setText(R.string.incoming_call);
        }

        connect.setImageResource(R.drawable.connect_unselect);
        cancel.setImageResource(R.drawable.cancel_unselect);

        if (settings.getBoolean("show_notification", false)) {
            showNotification();
        }

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPressed && isAppInstalled(LaunchActivity.this, "com.google.android.googlequicksearchbox")) {
                    isPressed = true;

                    connect.setImageResource(R.drawable.connect_select);

                    if (!AlarmReceiver.isPlaying()) {
                        m = MediaPlayer.create(getApplicationContext(), R.raw.tone);

                        m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                                status.setText(R.string.connecting);
                            }
                        });

                        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    } else {
                        AlarmReceiver.stopRingtone(getApplicationContext());
                        notificationManager.cancel(1);
                        alarmManager.cancel(pendingIntent);

                        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel.setImageResource(R.drawable.cancel_select);
                AlarmReceiver.stopRingtone(getApplicationContext());
                notificationManager.cancel(1);
                alarmManager.cancel(pendingIntent);

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        imageViewLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingIntent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LangContext.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m != null)
            m.release();
        AlarmReceiver.stopRingtone(this);
        notificationManager.cancel(1);
        aniHandle.removeCallbacks(aniRunnable);
    }

    @Override
    protected void onResume() {
        if (isPressed) {
            status.setText(R.string.disconnected);
        } else if (!isAppInstalled(LaunchActivity.this, "com.google.android.googlequicksearchbox")) {
            status.setText(R.string.google_app_error);
        } else if (AlarmReceiver.isPlaying()) {
            status.setText(R.string.incoming_call);
        } else {
            status.setText(R.string.call);
        }
        isPressed = false;
        connect.setImageResource(R.drawable.connect_unselect);
        cancel.setImageResource(R.drawable.cancel_unselect);
        super.onResume();
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.xp2)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text));
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
}