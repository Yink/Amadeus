package com.example.yink.amadeus;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LaunchActivity extends AppCompatActivity {
    ImageView connect, cancel;
    TextView status;
    AnimationDrawable logo;
    ImageView imageViewLogo;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            logo.start();
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
        imageViewLogo.setImageResource(R.drawable.logo_animation);
        logo = (AnimationDrawable) imageViewLogo.getDrawable();

        connect.setImageResource(R.drawable.connect_unselect);
        cancel.setImageResource(R.drawable.cancel_unselect);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer m = MediaPlayer.create(getApplicationContext(), R.raw.tone);

                connect.setImageResource(R.drawable.connect_select);

                m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        status.setText(R.string.connect);
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
    }

    @Override
    protected void onResume(){
        connect.setImageResource(R.drawable.connect_unselect);
        cancel.setImageResource(R.drawable.cancel_unselect);
        status.setText(R.string.call);
        super.onResume();
    }
}
