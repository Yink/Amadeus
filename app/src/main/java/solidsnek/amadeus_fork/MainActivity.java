package solidsnek.amadeus_fork;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.speech.RecognizerIntent;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String TAG = "Amadeus";
    ImageView kurisu;
    AnimationDrawable animation;

    private SpeechRecognizer sr;
    protected static final int REQ_CODE_SPEECH_INPUT = 1;

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
                promptSpeechInput();
            }
        });
    }

    @Override
    protected void onDestroy() {
        //tts.shutdown();
        super.onDestroy();
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "solidsnek.amadeus_fork");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        sr.startListening(intent);
    }

    public void play(int raw, int sprite) {
        try {
            MediaPlayer m = MediaPlayer.create(getApplicationContext(), raw);

            kurisu.setImageResource(sprite);

            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

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
            if (str.equals("hello")) {
                play(R.raw.haro, R.drawable.kurisu_9);
            }
            if (str.equals("hey")) {
                play(R.raw.hai, R.drawable.kurisu_9);
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
