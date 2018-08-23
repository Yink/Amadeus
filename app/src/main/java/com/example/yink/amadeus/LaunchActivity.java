package com.example.yink.amadeus;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LaunchActivity extends AppCompatActivity {

    private static final String GOOGLE_PACKAGE_NAME = "com.google.android.googlequicksearchbox";
    private static final String DEFAULT_ASSAIST = "com.google.android.googlequicksearchbox/com.google.android.voiceinteraction.GsaVoiceInteractionService";
    private static final String CHANNEL_ID = "amadeus_channel_icon";

    private ImageView connect, cancel, logo;
    private TextView status;
    private Boolean isPressed = false;
    private MediaPlayer m;
    private final Handler aniHandle = new Handler();

    private int i = 0;

    private final Runnable aniRunnable = new Runnable() {
        public void run() {
            final int DURATION = 20;
            if (i < 39) {
                i++;
                String imgName = "logo" + Integer.toString(i);
                int id = getResources().getIdentifier(imgName, "drawable", getPackageName());
                logo.setImageDrawable((ContextCompat.getDrawable(LaunchActivity.this, id)));
                aniHandle.postDelayed(this, DURATION);
            }
        }
    };

    private boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isDefaultAssistApp(Context context, String app){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }
        final String assistant = Settings.Secure.getString(context.getContentResolver(), "assistant");
        if (assistant.equals(app)) {
            return true;
        }
        Toast.makeText(context, getString(R.string.assist_app_error), Toast.LENGTH_LONG).show();
        startActivity(new Intent(Settings.ACTION_VOICE_INPUT_SETTINGS));
        return false;
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        connect = findViewById(R.id.imageView_connect);
        cancel = findViewById(R.id.imageView_cancel);
        status = findViewById(R.id.textView_call);
        logo = findViewById(R.id.imageView_logo);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final Window win = getWindow();

        aniHandle.post(aniRunnable);

        if (!isAppInstalled(LaunchActivity.this, GOOGLE_PACKAGE_NAME)) {
            status.setText(R.string.google_app_error);
        } else if (!isDefaultAssistApp(LaunchActivity.this, DEFAULT_ASSAIST)){
            status.setText(R.string.assist_app_error);
        } else if (settings.getBoolean("show_notification", false)) {
            showNotification();
        }

        if (Alarm.isPlaying()) {
            status.setText(R.string.incoming_call);
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        connect.setOnClickListener(view -> {
            if (!isPressed && isAppInstalled(LaunchActivity.this, GOOGLE_PACKAGE_NAME)
                    && isDefaultAssistApp(LaunchActivity.this, DEFAULT_ASSAIST)) {
                isPressed = true;

                connect.setImageResource(R.drawable.connect_select);

                if (!Alarm.isPlaying()) {
                    m = MediaPlayer.create(LaunchActivity.this, R.raw.tone);

                    m.setOnPreparedListener(mp -> {
                        mp.start();
                        status.setText(R.string.connecting);
                    });

                    m.setOnCompletionListener(mp -> {
                        mp.release();
                        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                        startActivity(intent);
                    });
                } else {
                    Alarm.cancel(LaunchActivity.this);
                    win.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
                    win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        cancel.setOnClickListener(view -> {
            cancel.setImageResource(R.drawable.cancel_select);
            Alarm.cancel(getApplicationContext());
            win.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        logo.setOnClickListener(view -> {
            Intent settingIntent = new Intent(LaunchActivity.this, SettingsActivity.class);
            startActivity(settingIntent);
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ContextLocalWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        final Window win = getWindow();

        if (m != null) {
            m.release();
        }

        Alarm.cancel(LaunchActivity.this);
        win.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        aniHandle.removeCallbacks(aniRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isPressed) {
            status.setText(R.string.disconnected);
        } else if (!isAppInstalled(LaunchActivity.this, GOOGLE_PACKAGE_NAME)) {
            status.setText(R.string.google_app_error);
        } else if (!isDefaultAssistApp(LaunchActivity.this, DEFAULT_ASSAIST)){
            status.setText(R.string.assist_app_error);
        } else if (Alarm.isPlaying()) {
            status.setText(R.string.incoming_call);
        } else {
            status.setText(R.string.call);
        }

        isPressed = false;
        connect.setImageResource(R.drawable.connect_unselect);
        cancel.setImageResource(R.drawable.cancel_unselect);
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, getString(R.string.pref_notification), NotificationManager.IMPORTANCE_LOW));
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(LaunchActivity.this, CHANNEL_ID)
                .setSmallIcon(R.drawable.xp2)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text))
                .setChannelId(CHANNEL_ID);
        Intent resultIntent = new Intent(LaunchActivity.this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(LaunchActivity.this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(0, builder.build());
    }
}