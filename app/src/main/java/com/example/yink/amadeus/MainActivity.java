package com.example.yink.amadeus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;



public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    String TAG = "Amadeus";
    TextToSpeech tts;
    ImageView kurisu;
    AnimationDrawable animation;
    HashMap<String, String> map;
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

    ArrayList<VoiceLine> voiceLines = new ArrayList<>();


    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        if(hasFocus)
            speak(new VoiceLine("Hallo.",happy));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kurisu = (ImageView) findViewById(R.id.imageView_kurisu);
        setupLines();
        map = new HashMap<String, String>();
        tts = new TextToSpeech(this, this);
        kurisu.setImageResource(R.drawable.kurisu_1);
        animation = (AnimationDrawable) kurisu.getDrawable();
        kurisu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random randomgen = new Random();
                speak(voiceLines.get(randomgen.nextInt(voiceLines.size())));
            }});
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
                            kurisu.setImageDrawable(animation.getFrame(0));
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

    public void speak(VoiceLine line){
        switch (line.getState()){
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
                tts.setPitch(1.5f);
                kurisu.setImageResource(R.drawable.kurisu_9);
                break;
            case angry:
                kurisu.setImageResource(R.drawable.kurisu_10);
                break;
            case blush:
                tts.setPitch(1.5f);
                tts.setSpeechRate(1.3f);
                kurisu.setImageResource(R.drawable.kurisu_11);
                break;
            case side:
                kurisu.setImageResource(R.drawable.kurisu_12);
                break;
            default:kurisu.setImageResource(R.drawable.kurisu_2);

        }
        animation = (AnimationDrawable) kurisu.getDrawable();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"UniqueID");
        tts.speak(line.getText(),TextToSpeech.QUEUE_FLUSH,map);
        tts.setPitch(1.05f);
        tts.setSpeechRate(1.0f);
    }

    @Override
    protected void onDestroy(){
        tts.shutdown();
        super.onDestroy();
    }

    protected void setupLines(){
        voiceLines.add(new VoiceLine("Konban wa, Okabe. Okaeri!",happy));
        voiceLines.add(new VoiceLine("Okabe, sabishi des!",sad));
        voiceLines.add(new VoiceLine("Tsumaranai",indifferent));
        voiceLines.add(new VoiceLine("Okabe ga ski",blush));
        voiceLines.add(new VoiceLine("Chigau",angry));
    }
}
