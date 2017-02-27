package com.example.yink.amadeus;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    TextToSpeech tts;
    ImageView kurisu;
    AnimationDrawable animation;
    HashMap<String, String> map;
    int state = 0;
    private final int eyes_closed = 0;
    private final int normal = 1;
    private final int sad = 2;
    private final int indifferent = 3;
    private final int wink = 4;
    private final int pissed = 5;
    private final int annoyed = 6;
    private final int disappointed = 7;
    private final int happy = 8;
    private final int angry = 9;
    private final int blush = 10;
    private final int side = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kurisu = (ImageView) findViewById(R.id.imageView_kurisu);
        map = new HashMap<String, String>();
        tts = new TextToSpeech(this, this);
        kurisu.setImageResource(R.drawable.kurisu_1);
        animation = (AnimationDrawable) kurisu.getDrawable();
        kurisu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak("Konban wa, Okabe. Okaeri!",state);
                state=(state+1)%12;}

        });
    }

    @Override
    public void onInit(int status) {
        if(status==TextToSpeech.SUCCESS){
            tts.setLanguage(Locale.JAPAN);
            tts.setPitch(1.05f);
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           animation.start();
                       }
                   });
                }

                @Override
                public void onDone(String s) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animation.stop();
                        }
                    });
                }

                @Override
                public void onError(String s) {
                    animation.stop();
                }
            });
        }
    }

    public void speak(String text, int state){
        switch (state){
            case eyes_closed:
                kurisu.setImageResource(R.drawable.kurisu_1);
                break;
            case normal:
                kurisu.setImageResource(R.drawable.kurisu_2);
                break;
            case sad:
                kurisu.setImageResource(R.drawable.kurisu_3);
                break;
            case indifferent:
                kurisu.setImageResource(R.drawable.kurisu_4);
                break;
            case wink:
                kurisu.setImageResource(R.drawable.kurisu_5);
                break;
            case pissed:
                kurisu.setImageResource(R.drawable.kurisu_6);
                break;
            case annoyed:
                kurisu.setImageResource(R.drawable.kurisu_7);
                break;
            case disappointed:
                kurisu.setImageResource(R.drawable.kurisu_8);
                break;
            case happy:
                kurisu.setImageResource(R.drawable.kurisu_9);
                break;
            case angry:
                kurisu.setImageResource(R.drawable.kurisu_10);
                break;
            case blush:
                kurisu.setImageResource(R.drawable.kurisu_11);
                break;
            case side:
                kurisu.setImageResource(R.drawable.kurisu_12);
                break;
            default:kurisu.setImageResource(R.drawable.kurisu_2);

        }
        animation = (AnimationDrawable) kurisu.getDrawable();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"UniqueID");
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,map);
    }

    @Override
    protected void onDestroy(){
        tts.shutdown();
        super.onDestroy();
    }
}
