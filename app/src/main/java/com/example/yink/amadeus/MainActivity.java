package com.example.yink.amadeus;

/*
 * Big thanks to https://github.com/RIP95 aka Emojikage
 */

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    final String TAG = "Amadeus";
    final int REQUEST_PERMISSION_RECORD_AUDIO = 1;
    TextView subtitles;
    ImageView kurisu;
    AnimationDrawable animation;
    Handler handler;
    Boolean isLoop = false;
    Boolean isSpeaking = false;
    ArrayList<VoiceLine> voiceLines = new ArrayList<>();
    int shaman_girls = -1;
    Random randomgen = new Random();
    SharedPreferences sharedPreferences;
    private SpeechRecognizer sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kurisu = (ImageView) findViewById(R.id.imageView_kurisu);
        kurisu.setImageResource(R.drawable.kurisu9a);
        subtitles = (TextView) findViewById(R.id.textView_subtitles);
        ImageView imageViewSubtitles = (ImageView) findViewById(R.id.imageView_subtitles);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!sharedPreferences.getBoolean("show_subtitles", false)) {
            imageViewSubtitles.setVisibility(View.INVISIBLE);
        }
        handler = new Handler();
        setupLines();
        speak(voiceLines.get(0));

        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_RECORD_AUDIO);
        }

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

        final Runnable loop = new Runnable() {
            @Override
            public void run() {
                if (isLoop) {
                    speak(voiceLines.get(randomgen.nextInt(voiceLines.size())));
                    handler.postDelayed(this, 5000 + randomgen.nextInt(5) * 1000);
                }
            }
        };
        kurisu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    MainActivity host = (MainActivity) view.getContext();

                    int permissionCheck = ContextCompat.checkSelfPermission(host,
                            Manifest.permission.RECORD_AUDIO);

                    /* Input while loop producing bugs and mixes with output */
                    if (!isLoop && !isSpeaking) {
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            promptSpeechInput();
                        } else {
                            speak(new VoiceLine(R.raw.daga_kotowaru, Mood.PISSED, R.string.line_but_i_refuse));
                        }
                    }

                } else if (!isLoop && !isSpeaking) {
                    promptSpeechInput();
                }
            }});


        kurisu.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isLoop && !isSpeaking) {
                    isLoop = true;
                    handler.post(loop);
                } else {
                    handler.removeCallbacks(loop);
                    isLoop = false;
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (sr != null)
            sr.destroy();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        isLoop = false;
        super.onStop();
    }

    @Override
    protected void onPause() {
        isLoop = false;
        super.onPause();
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP");

        sr.startListening(intent);
    }

    public void speak(VoiceLine line) {
        try {
            MediaPlayer m = MediaPlayer.create(getApplicationContext(), line.getId());
            final Visualizer v = new Visualizer(m.getAudioSessionId());

            if (sharedPreferences.getBoolean("show_subtitles", false)) {
                subtitles.setText(line.getSubtitle());
            }

            Resources res = getResources();
            animation = (AnimationDrawable) Drawable.createFromXml(res, res.getXml(line.getMood()));

            if (m.isPlaying()) {
                m.stop();
                m.release();
                v.setEnabled(false);
                m = new MediaPlayer();
            }

            m.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isSpeaking = true;
                    mp.start();
                    v.setEnabled(true);
                }
            });

            m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isSpeaking = false;
                    mp.release();
                    v.setEnabled(false);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             kurisu.setImageDrawable(animation.getFrame(0));
                        }
                    });
                }
            });


            v.setEnabled(false);
            v.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            v.setDataCaptureListener(
                    new Visualizer.OnDataCaptureListener() {
                        public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                            int sum = 0;
                            for (int i = 1; i < bytes.length; i++) {
                                sum += bytes[i] + 128;
                            }
                            // The normalized volume
                            final float normalized = sum / (float) bytes.length;

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (normalized > 50) {
                                        // Todo: Maybe choose sprite based on previous choice and volume instead of random
                                        kurisu.setImageDrawable(animation.getFrame((int) Math.ceil(Math.random() * 2)));
                                    } else {
                                        kurisu.setImageDrawable(animation.getFrame(0));
                                    }
                                }
                            });
                        }
                        public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) { }
                    }, Visualizer.getMaxCaptureRate() / 2, true, false);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void answerSpeech(String input) {
        List<String> greeting = Arrays.asList("ハロー", "おはよう", "こんにちは", "こんばんは");
        Log.e(TAG, input);
        Random randomGen = new Random();
        if (input.contains("クリスティーナ")) {
            switch (randomGen.nextInt(4)) {
                case 0:
                    speak(voiceLines.get(10));
                    break;
                case 1:
                    speak(voiceLines.get(13));
                    break;
                case 2:
                    speak(voiceLines.get(14));
                    break;
                case 3:
                    speak(voiceLines.get(15));
                    break;
            }
        } else if (input.contains("ぬるぽ")) {
            shaman_girls += 1;
            if (shaman_girls < 5) {
                switch (randomGen.nextInt(2)) {
                    case 0:
                        speak(voiceLines.get(9));
                        break;
                    case 1:
                        speak(voiceLines.get(42));
                        break;
                }
            } else {
                switch (shaman_girls) {
                    case 5:
                        speak(new VoiceLine(R.raw.leskinen_awesome, Mood.WINKING, R.string.line_Leskinen_awesome));
                        break;
                    case 6:
                        speak(new VoiceLine(R.raw.leskinen_nice, Mood.WINKING, R.string.line_Leskinen_nice));
                        break;
                    case 7:
                        speak(new VoiceLine(R.raw.leskinen_oh_no, Mood.WINKING, R.string.line_Leskinen_oh_no));
                        break;
                    case 8:
                        speak(new VoiceLine(R.raw.leskinen_shaman, Mood.WINKING, R.string.line_Leskinen_shaman));
                        break;
                    case 9:
                        speak(new VoiceLine(R.raw.leskinen_holy_cow, Mood.WINKING, R.string.line_Leskinen_holy_cow));
                        shaman_girls = 0;
                        break;
                }
            }
        } else if (input.contains("アットチャンネル") || input.contains("栗ご飯") || input.contains("カメハメハ")) {
            speak(voiceLines.get(30 + randomGen.nextInt(2)));
        } else if (input.contains("サリエリ") || input.contains("真帆") || input.contains("比屋定")) {
            speak(voiceLines.get(26 + randomGen.nextInt(4)));
        } else if (input.contains("タイムマシーン") || input.contains("cern") || input.contains("タイムトラベル")) {
            speak(voiceLines.get(32 + randomGen.nextInt(5)));
        } else if (input.contains("メモリー") || input.contains("アマデウス") || input.contains("サイエンス")) {
            speak(voiceLines.get(37 + randomGen.nextInt(5)));
        } else if (greeting.contains(input)) {
            switch (randomGen.nextInt(4)) {
                case 0:
                    speak(voiceLines.get(12));
                    break;
                case 1:
                    speak(voiceLines.get(24));
                    break;
                case 2:
                    speak(voiceLines.get(25));
                    break;
                case 3:
                    speak(voiceLines.get(43));
                    break;
            }
        } else if (input.contains("ナイスボディ") || input.contains("ほっと") || input.contains("セクシー") || input.contains("ボビーズ") || input.contains("おっぱい")) {
            switch (randomGen.nextInt(3)) {
                case 0:
                    speak(voiceLines.get(2));
                    break;
                case 1:
                    speak(voiceLines.get(5));
                    break;
                case 2:
                    speak(voiceLines.get(11));
                    break;
            }
        } else {
            speak(voiceLines.get(16 + randomGen.nextInt(7)));
        }
    }

    private void setupLines() {
        voiceLines.add(new VoiceLine(R.raw.hello, Mood.HAPPY, R.string.line_hello));
        voiceLines.add(new VoiceLine(R.raw.daga_kotowaru, Mood.ANNOYED, R.string.line_but_i_refuse));
        voiceLines.add(new VoiceLine(R.raw.devilish_pervert, Mood.ANGRY, R.string.line_devilish_pervert));
        voiceLines.add(new VoiceLine(R.raw.i_guess, Mood.INDIFFERENT, R.string.line_i_guess));
        voiceLines.add(new VoiceLine(R.raw.nice, Mood.WINKING, R.string.line_nice));
        voiceLines.add(new VoiceLine(R.raw.pervert_confirmed, Mood.PISSED, R.string.line_pervert_confirmed)); //5
        voiceLines.add(new VoiceLine(R.raw.sorry, Mood.SAD, R.string.line_sorry));
        voiceLines.add(new VoiceLine(R.raw.sounds_tough, Mood.SIDE, R.string.line_sounds_tough));
        voiceLines.add(new VoiceLine(R.raw.this_guy_hopeless, Mood.DISAPPOINTED, R.string.line_this_guy_hopeless));
        voiceLines.add(new VoiceLine(R.raw.gah, Mood.INDIFFERENT, R.string.line_gah));
        voiceLines.add(new VoiceLine(R.raw.dont_add_tina, Mood.ANGRY, R.string.line_dont_add_tina)); //10
        voiceLines.add(new VoiceLine(R.raw.pervert_idot_wanttodie, Mood.ANGRY, R.string.line_pervert_idiot_wanttodie));
        voiceLines.add(new VoiceLine(R.raw.pleased_to_meet_you, Mood.SIDED_PLEASANT, R.string.line_pleased_to_meet_you));
        voiceLines.add(new VoiceLine(R.raw.who_the_hell_christina, Mood.PISSED, R.string.line_who_the_hell_christina));
        voiceLines.add(new VoiceLine(R.raw.why_christina, Mood.PISSED, R.string.line_why_christina));
        voiceLines.add(new VoiceLine(R.raw.christina, Mood.ANNOYED, R.string.line_christina)); //15
        voiceLines.add(new VoiceLine(R.raw.ask_me_whatever, Mood.HAPPY, R.string.line_ask_me_whatever));
        voiceLines.add(new VoiceLine(R.raw.could_i_help, Mood.HAPPY, R.string.line_could_i_help));
        voiceLines.add(new VoiceLine(R.raw.ask_me_whatever, Mood.HAPPY, R.string.line_ask_me_whatever));
        voiceLines.add(new VoiceLine(R.raw.what_do_you_want, Mood.HAPPY, R.string.line_what_do_you_want));
        voiceLines.add(new VoiceLine(R.raw.what_is_it, Mood.HAPPY, R.string.line_what_is_it)); //20
        voiceLines.add(new VoiceLine(R.raw.heheh, Mood.WINKING, R.string.line_heheh));
        voiceLines.add(new VoiceLine(R.raw.huh_why_say, Mood.SIDED_WORRIED, R.string.line_huh_why_say));
        voiceLines.add(new VoiceLine(R.raw.you_sure, Mood.SIDED_WORRIED, R.string.line_you_sure));
        voiceLines.add(new VoiceLine(R.raw.nice_to_meet_okabe, Mood.SIDED_PLEASANT, R.string.line_nice_to_meet_okabe));
        voiceLines.add(new VoiceLine(R.raw.look_forward_to_working, Mood.HAPPY, R.string.line_look_forward_to_working)); //25
        voiceLines.add(new VoiceLine(R.raw.senpai_question, Mood.SIDE, R.string.line_senpai_question));
        voiceLines.add(new VoiceLine(R.raw.senpai_questionmark, Mood.SIDE, R.string.line_senpai_question_mark));
        voiceLines.add(new VoiceLine(R.raw.senpai_what_we_talkin, Mood.SIDED_WORRIED, R.string.line_senpai_what_we_talkin));
        voiceLines.add(new VoiceLine(R.raw.senpai_who_is_this, Mood.NORMAL, R.string.line_senpai_who_is_this));
        voiceLines.add(new VoiceLine(R.raw.senpai_please_dont_tell, Mood.BLUSH, R.string.line_senpai_please_dont_tell)); //30
        voiceLines.add(new VoiceLine(R.raw.still_not_happy, Mood.BLUSH, R.string.line_still_not_happy));
        voiceLines.add(new VoiceLine(R.raw.tm_nonsense, Mood.DISAPPOINTED, R.string.line_tm_nonsense));
        voiceLines.add(new VoiceLine(R.raw.tm_not_possible, Mood.DISAPPOINTED, R.string.line_tm_not_possible));
        voiceLines.add(new VoiceLine(R.raw.tm_scientist_no_evidence, Mood.NORMAL, R.string.line_tm_scientist_no_evidence));
        voiceLines.add(new VoiceLine(R.raw.tm_we_dont_know, Mood.NORMAL, R.string.line_tm_we_dont_know)); //35
        voiceLines.add(new VoiceLine(R.raw.tm_you_said, Mood.SIDED_WORRIED, R.string.line_tm_you_said));
        voiceLines.add(new VoiceLine(R.raw.humans_software, Mood.NORMAL, R.string.line_humans_software));
        voiceLines.add(new VoiceLine(R.raw.memory_complex, Mood.INDIFFERENT, R.string.line_memory_complex));
        voiceLines.add(new VoiceLine(R.raw.secret_diary, Mood.INDIFFERENT, R.string.line_secret_diary));
        voiceLines.add(new VoiceLine(R.raw.modifying_memories_impossible, Mood.INDIFFERENT, R.string.line_modifying_memories_impossible)); //40
        voiceLines.add(new VoiceLine(R.raw.memories_christina, Mood.WINKING, R.string.line_memories_christina));
        voiceLines.add(new VoiceLine(R.raw.gah_extended, Mood.BLUSH, R.string.line_gah_extended));
        voiceLines.add(new VoiceLine(R.raw.should_christina, Mood.PISSED, R.string.line_should_christina));
    }

    private class Mood {
        static final int HAPPY = R.drawable.kurisu_9;
        static final int PISSED = R.drawable.kurisu_6;
        static final int ANNOYED = R.drawable.kurisu_7;
        static final int ANGRY = R.drawable.kurisu_10;
        static final int BLUSH = R.drawable.kurisu_11;
        /* TODO: How should we name this mood?.. */
        static final int SIDE = R.drawable.kurisu_12;
        static final int SAD = R.drawable.kurisu_3;
        static final int NORMAL = R.drawable.kurisu_2;
        static final int SLEEPY = R.drawable.kurisu_1;
        static final int WINKING = R.drawable.kurisu_5;
        static final int DISAPPOINTED = R.drawable.kurisu_8;
        static final int INDIFFERENT = R.drawable.kurisu_4;
        static final int SIDED_PLEASANT = R.drawable.kurisu_15;
        static final int SIDED_WORRIED = R.drawable.kurisu_17;
    }

    private class listener implements RecognitionListener {
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
            sr.cancel();
            speak(voiceLines.get(6));
        }
        public void onResults(Bundle results) {
            String input = "";
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            input += data.get(0);
            answerSpeech(input);
        }
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent " + eventType);
        }

    }
}
