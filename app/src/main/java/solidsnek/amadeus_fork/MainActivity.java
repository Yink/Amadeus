package solidsnek.amadeus_fork;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.speech.RecognizerIntent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String TAG = "Amadeus";
    ImageView kurisu;
    AnimationDrawable animation;

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
    }

    /* Don't forget about permission to use audio! */
    private SpeechRecognizer sr;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            speak(R.raw.haro, Mood.SIDED_PLEASANT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kurisu = (ImageView) findViewById(R.id.imageView_kurisu);
        kurisu.setImageResource(R.drawable.kurisu_1);
        animation = (AnimationDrawable) kurisu.getDrawable();

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

        kurisu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity host = (MainActivity) view.getContext();

                int permissionCheck = ContextCompat.checkSelfPermission(host,
                        Manifest.permission.RECORD_AUDIO);

                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    promptSpeechInput();
                } else {
                    speak(R.raw.daga_kotowaru, Mood.PISSED);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (sr != null)
            sr.destroy();

        super.onDestroy();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP");

        sr.startListening(intent);
    }

    public void speak(int raw, int mood) {
        try {
            MediaPlayer m = MediaPlayer.create(getApplicationContext(), raw);

            kurisu.setImageResource(mood);

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

    class listener implements RecognitionListener {
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

            String[] greetingArr = new String[]{"ハロー", "おはよう", "こんにちは", "こんばんは"};
            List<String> greeting = Arrays.asList(greetingArr);

            if (greeting.contains(str)) {
                speak(R.raw.haro, Mood.HAPPY);
            }
            if (str.equals("クリス")) {
                speak(R.raw.hai, Mood.NORMAL);
            }
        }
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }
    }

}