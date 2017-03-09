package com.example.yink.amadeus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class LaunchActivity extends AppCompatActivity {
    ImageView connect, cancel, imageViewLogo;
    TextView status;
    AnimationDrawable logo;
    Boolean isPressed = false;
    SharedPreferences sharedPreferences;
    MediaPlayer m;
    int i = 0;
    int id;
    int duration = 20;
    Runnable aniRunnable = new Runnable() {
        public void run() {
            imageViewLogo.setImageDrawable((getResources().getDrawable(id)));
            animate(imageViewLogo);
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

    protected void animate(ImageView view) {
        if (i < 39) {
            i += 1;
            String imgName = "logo" + Integer.toString(i);
            id = getResources().getIdentifier(imgName, "drawable", getPackageName());
            view.postDelayed(aniRunnable, duration);
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
        /*
         *  Reported OOM on 2K+ resolution devices.
         *  Looks like better to change it to static image for now.
         */
        /*logo = (AnimationDrawable) imageViewLogo.getDrawable();
        logo.start();*/
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        animate(imageViewLogo);
        if (!isAppInstalled(LaunchActivity.this, "com.google.android.googlequicksearchbox")) {
            status.setText(R.string.google_app_error);
        }

        connect.setImageResource(R.drawable.connect_unselect);
        cancel.setImageResource(R.drawable.cancel_unselect);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPressed && isAppInstalled(LaunchActivity.this, "com.google.android.googlequicksearchbox")) {
                    try {
                        isPressed = true;
                        m = MediaPlayer.create(getApplicationContext(), R.raw.tone);

                        connect.setImageResource(R.drawable.connect_select);

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
                                Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel.setImageResource(R.drawable.cancel_select);

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
        super.attachBaseContext(LangContextWrapper.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        if (m != null)
            m.release();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (isPressed) {
            status.setText(R.string.disconnected);
        } else if (!isAppInstalled(LaunchActivity.this, "com.google.android.googlequicksearchbox")) {
            status.setText(R.string.google_app_error);
        } else {
            status.setText(R.string.call);
        }
        isPressed = false;
        connect.setImageResource(R.drawable.connect_unselect);
        cancel.setImageResource(R.drawable.cancel_unselect);
        super.onResume();
    }

    /* Reported OOM on 2K+ resolution devices */
    /*
    @Override
    protected void onPause() {
        //to enable the to collect the animation frames
        if (logo != null && logo.isRunning())
            logo.stop();
        imageViewLogo.setImageResource(R.drawable.logo39);
        logo = null;
        super.onPause();
    }*/

    @SuppressWarnings("deprecation")
    private void setLocale(Context context, String lang) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale myLocale = new Locale(lang);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(myLocale);
        } else {
            config.locale = myLocale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = context.createConfigurationContext(config);
        } else {
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

}