package com.example.yink.amadeus;

/**
 * Big thanks to https://github.com/RIP95 aka Emojikage
 */

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.SystemClock;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    final String TAG = "Amadeus";
    MediaPlayer mediaPlayer;
    ImageView kurisu;
    AnimationDrawable animation;
    Handler handler;
    Boolean looping = false;

    private SpeechRecognizer sr;
    protected static final int REQ_CODE_SPEECH_INPUT = 1;

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
        if(hasFocus){
            speak(new VoiceLine(R.raw.hello,happy));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kurisu = (ImageView) findViewById(R.id.imageView_kurisu);
        handler = new Handler();
        setupLines();
        kurisu.setImageResource(R.drawable.kurisu_1);
        animation = (AnimationDrawable) kurisu.getDrawable();

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

        final Runnable loop = new Runnable() {
            @Override
            public void run() {
                Random randomgen = new Random();
                speak(voiceLines.get(randomgen.nextInt(voiceLines.size())));
                handler.postDelayed(this,5000+randomgen.nextInt(5)*1000);
            }
        };
        kurisu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();

            }});

        kurisu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!looping){
                    looping =true;
                    handler.post(loop);
                }else{
                    handler.removeCallbacks(loop);
                    looping = false;
                }
                return true;
            }
        });
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
        mediaPlayer = mediaPlayer.create(this,line.getId());
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                animation.stop();
                kurisu.setImageDrawable(animation.getFrame(0));
                mediaPlayer.release();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animation.start();
                    }
                });
            }
        });
        mediaPlayer.start();
        animation.start();
    }

    private void answerSpeech(String request){
        Log.e(TAG,request);
        if (request.equals("ハロー")) {
            speak(new VoiceLine(R.raw.hello,happy));
        }else if (request.equals("ナイスボディ")) {
            speak(new VoiceLine(R.raw.devilish_pervert,angry));
        }else if (request.equals("クリスティーナ")) {
            speak(new VoiceLine(R.raw.this_guy_hopeless,disappointed));
        }
    }

    @Override
    protected void onDestroy(){
        if(sr!=null)
            sr.destroy();
        super.onDestroy();
    }

    @Override
    protected void onStop(){
        mediaPlayer.release();
        mediaPlayer = null;
        super.onStop();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    private void setupLines(){
        voiceLines.add(new VoiceLine(R.raw.daga_kotowaru,annoyed));
        voiceLines.add(new VoiceLine(R.raw.devilish_pervert,angry));
        voiceLines.add(new VoiceLine(R.raw.i_guess,indifferent));
        voiceLines.add(new VoiceLine(R.raw.nice,wink));
        voiceLines.add(new VoiceLine(R.raw.pervert_confirmed,pissed));
        voiceLines.add(new VoiceLine(R.raw.sorry,sad));
        voiceLines.add(new VoiceLine(R.raw.sounds_tough,side));
        voiceLines.add(new VoiceLine(R.raw.this_guy_hopeless,disappointed));
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));

        sr.startListening(intent);
    }

    public class listener implements RecognitionListener {
        final String TAG = "Amadeus.listener";

        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error) {
            Log.d(TAG,  "error " +  error);
        }
        public void onResults(Bundle results) {
            String str = "";
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            str += data.get(0);
            answerSpeech(str);

        }
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }

    }



}

