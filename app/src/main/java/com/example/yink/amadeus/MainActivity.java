package com.example.yink.amadeus;

/**
 * Big thanks to https://github.com/RIP95 aka Emojikage
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    final String TAG = "Amadeus";
    ImageView kurisu;
    AnimationDrawable animation;
    Handler handler;
    Boolean looping = false;
    ArrayList<VoiceLine> voiceLines = new ArrayList<>();
    private SpeechRecognizer sr;

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        if(hasFocus){
            speak(new VoiceLine(R.raw.hello, Mood.HAPPY));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kurisu = (ImageView) findViewById(R.id.imageView_kurisu);
        handler = new Handler();
        setupLines();

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

        final Runnable loop = new Runnable() {
            @Override
            public void run() {
                if (looping) {
                Random randomgen = new Random();
                speak(voiceLines.get(randomgen.nextInt(voiceLines.size())));
                    handler.postDelayed(this, 5000 + randomgen.nextInt(5) * 1000);
                }
            }
        };
        kurisu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity host = (MainActivity) view.getContext();

                int permissionCheck = ContextCompat.checkSelfPermission(host,
                        Manifest.permission.RECORD_AUDIO);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    promptSpeechInput();
                } else {
                    speak(new VoiceLine(R.raw.daga_kotowaru, Mood.PISSED));
                }

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

    @Override
    protected void onDestroy(){
        if(sr!=null)
            sr.destroy();
        looping = false;
        super.onDestroy();
    }

    @Override
    protected void onStop(){
        looping = false;
        super.onStop();
    }

    @Override
    protected void onPause(){
        looping = false;
        super.onPause();
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

    public void speak(VoiceLine line) {
        try {
            MediaPlayer m = MediaPlayer.create(getApplicationContext(), line.getId());

            kurisu.setImageResource(line.getMood());

            animation = (AnimationDrawable) kurisu.getDrawable();

            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

            m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animation.start();
                        }
                    });
                }
            });

            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            animation.stop();
                            kurisu.setImageDrawable(animation.getFrame(0));
                        }
                    });
                }
            });

            m.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void answerSpeech(String request) {
        String[] greetingArr = new String[]{"ハロー", "おはよう", "こんにちは", "こんばんは"};
        List<String> greeting = Arrays.asList(greetingArr);
        Log.e(TAG, request);
        if (greeting.contains(request)) {
            speak(new VoiceLine(R.raw.hello, Mood.HAPPY));
        } else if (request.equals("ナイスボディ")) {
            speak(new VoiceLine(R.raw.devilish_pervert, Mood.ANGRY));
        } else if (request.contains("クリスティーナ")) {
            speak(new VoiceLine(R.raw.this_guy_hopeless, Mood.DISAPPOINTED));
        }
    }

    private void setupLines() {
        voiceLines.add(new VoiceLine(R.raw.daga_kotowaru, Mood.ANNOYED));
        voiceLines.add(new VoiceLine(R.raw.devilish_pervert, Mood.ANGRY));
        voiceLines.add(new VoiceLine(R.raw.i_guess, Mood.INDIFFERENT));
        voiceLines.add(new VoiceLine(R.raw.nice, Mood.WINKING));
        voiceLines.add(new VoiceLine(R.raw.pervert_confirmed, Mood.PISSED));
        voiceLines.add(new VoiceLine(R.raw.sorry, Mood.SAD));
        voiceLines.add(new VoiceLine(R.raw.sounds_tough, Mood.SIDE));
        voiceLines.add(new VoiceLine(R.raw.this_guy_hopeless, Mood.DISAPPOINTED));
    }

    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults");
    }

    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent " + eventType);
    }

    private class Mood {
        public static final int HAPPY = R.drawable.kurisu_9;
        public static final int PISSED = R.drawable.kurisu_6;
        public static final int ANNOYED = R.drawable.kurisu_7;
        public static final int ANGRY = R.drawable.kurisu_10;
        public static final int BLUSH = R.drawable.kurisu_11;
        public static final int SIDE = R.drawable.kurisu_12;
        public static final int SAD = R.drawable.kurisu_3;
        public static final int NORMAL = R.drawable.kurisu_2;
        public static final int SLEEPY = R.drawable.kurisu_1;
        public static final int WINKING = R.drawable.kurisu_5;
        public static final int DISAPPOINTED = R.drawable.kurisu_8;
        public static final int INDIFFERENT = R.drawable.kurisu_4;
        public static final int SIDED_PLEASANT = R.drawable.kurisu_15;
        public static final int SIDED_WORRIED = R.drawable.kurisu_17;
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

