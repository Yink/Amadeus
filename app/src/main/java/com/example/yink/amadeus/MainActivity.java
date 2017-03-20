package com.example.yink.amadeus;

/*
 * Big thanks to https://github.com/RIP95 aka Emojikage
 */

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    final String TAG = "Amadeus";
    final int REQUEST_PERMISSION_RECORD_AUDIO = 1;
    TextView subtitles;
    ImageView kurisu;
    Boolean isLoop = false;
    Boolean isSpeaking = false;
    VoiceLine[] voiceLines = VoiceLine.Line.getLines();
    AnimationDrawable animation;
    int shaman_girls = -1;
    Random randomgen = new Random();
    SharedPreferences settings;
    String lang, recogLang;
    MediaPlayer m;
    String[] contextLang;
    private SpeechRecognizer sr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kurisu = (ImageView) findViewById(R.id.imageView_kurisu);
        subtitles = (TextView) findViewById(R.id.textView_subtitles);
        ImageView subtitlesBackground = (ImageView) findViewById(R.id.imageView_subtitles);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lang = settings.getString("lang", "ja");
        recogLang = settings.getString("recognition_lang", "ja-JP");
        contextLang = recogLang.split("-");
        if (!settings.getBoolean("show_subtitles", false)) {
            subtitlesBackground.setVisibility(View.INVISIBLE);
        }
        speak(voiceLines[VoiceLine.Line.HELLO]);

        final Handler handler = new Handler();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_RECORD_AUDIO);
        }
        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new listener());

        final Runnable loop = new Runnable() {
            @Override
            public void run() {
                if (isLoop) {
                    speak(voiceLines[randomgen.nextInt(voiceLines.length)]);
                    handler.postDelayed(this, 5000 + randomgen.nextInt(5) * 1000);
                }
            }
        };
        kurisu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    MainActivity host = (MainActivity) view.getContext();

                    int permissionCheck = ContextCompat.checkSelfPermission(host,
                            Manifest.permission.RECORD_AUDIO);

                    /* Input while loop producing bugs and mixes with output */
                    if (!isLoop && !isSpeaking) {
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            promptSpeechInput();
                        } else {
                            speak(voiceLines[VoiceLine.Line.DAGA_KOTOWARU]);
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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LangContext.wrap(newBase));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sr != null)
            sr.destroy();
        if (m != null)
            m.release();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isLoop = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isLoop = false;
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, recogLang);

        /* Temporary workaround for strange bug on 4.0.3-4.0.4 */
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            try {
                startActivityForResult(intent, 1);
            } catch (ActivityNotFoundException a) {
                a.printStackTrace();
            }
        } else {
            sr.startListening(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK && null != data) {

                    /* Switch language within current context for voice recognition */
                    Context context = LangContext.load(getApplicationContext(), contextLang[0]);

                    ArrayList<String> input = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    answerSpeech(input.get(0), context);
                }
                break;
            }

        }
    }

    public void speak(VoiceLine line) {
        try {
            m = MediaPlayer.create(getApplicationContext(), line.getId());
            final Visualizer v = new Visualizer(m.getAudioSessionId());

            if (settings.getBoolean("show_subtitles", false)) {
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

    private void openApp(String[] input) {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Context ctx = getApplicationContext();

        HashMap<String, Integer> dictionary = new HashMap<>();
        String corrected;
        Boolean found;
        dictionary.put("хром", 0);
        dictionary.put("календарь", 1);
        dictionary.put("часы", 2);
        dictionary.put("будильник", 2);
        dictionary.put("камеру", 3);

        String[] apps = {
            "chrome", "calendar", "clock", "camera"
        };

        for (ApplicationInfo packageInfo : packages) {
            /* TODO: Needs to be adjusted probably. */
            found = true;
            /* Look up words in dictionary and correct the input since we can't open some apps in other langs */
            for (String word: input) {
                if (dictionary.get(word) != null) {
                    corrected = apps[dictionary.get(word)].toLowerCase();
                } else {
                    corrected = word.toLowerCase();
                }
                if (!packageInfo.packageName.contains(corrected)) {
                    found = false;
                    break;
                }
            }
            if (found) {
                Intent app;
                speak(voiceLines[VoiceLine.Line.OK]);
                switch (packageInfo.packageName) {
                    /* Exceptional cases */
                    case "com.android.phone": {
                        app = new Intent(Intent.ACTION_DIAL, null);
                        ctx.startActivity(app);
                        break;
                    }
                    case "com.android.chrome": {
                        app = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                        /* Default browser might be different */
                        app.setPackage(packageInfo.packageName);
                        ctx.startActivity(app);
                        break;
                    }
                    default: {
                        app = ctx.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
                        /* Check if intent is not null to avoid crash */
                        if (app != null) {
                            app.addCategory(Intent.CATEGORY_LAUNCHER);
                            ctx.startActivity(app);
                        }
                        break;
                    }
                }
                /* Don't need to search for other ones, so break this loop */
                break;
            }
        }
    }

    private void answerSpeech(String input, Context context) {

        input = input.toLowerCase();
        if (input.contains(context.getString(R.string.christina))) {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.CHRISTINA],
                    voiceLines[VoiceLine.Line.WHY_CHRISTINA],
                    voiceLines[VoiceLine.Line.SHOULD_CHRISTINA],
                    voiceLines[VoiceLine.Line.NO_TINA]
            };
            speak(specificLines[randomgen.nextInt(specificLines.length)]);
        } else if (input.contains(context.getString(R.string.nullpo))) {
            shaman_girls += 1;
            if (shaman_girls < 5) {
                VoiceLine[] specificLines = {
                        voiceLines[VoiceLine.Line.GAH],
                        voiceLines[VoiceLine.Line.GAH_EXTENDED]
                };
                speak(specificLines[randomgen.nextInt(specificLines.length)]);
            } else {
                switch (shaman_girls) {
                    case 5:
                        speak(new VoiceLine(R.raw.leskinen_awesome, VoiceLine.Mood.WINKING, R.string.line_Leskinen_awesome));
                        break;
                    case 6:
                        speak(new VoiceLine(R.raw.leskinen_nice, VoiceLine.Mood.WINKING, R.string.line_Leskinen_nice));
                        break;
                    case 7:
                        speak(new VoiceLine(R.raw.leskinen_oh_no, VoiceLine.Mood.WINKING, R.string.line_Leskinen_oh_no));
                        break;
                    case 8:
                        speak(new VoiceLine(R.raw.leskinen_shaman, VoiceLine.Mood.WINKING, R.string.line_Leskinen_shaman));
                        break;
                    case 9:
                        speak(new VoiceLine(R.raw.leskinen_holy_cow, VoiceLine.Mood.WINKING, R.string.line_Leskinen_holy_cow));
                        shaman_girls = 0;
                        break;
                }
            }
        } else if (input.contains(context.getString(R.string.the_zombie))
                || input.contains(context.getString(R.string.celeb17))) {
            speak(voiceLines[VoiceLine.Line.DONT_CALL_ME_LIKE_THAT]);
        } else if (input.contains(context.getString(R.string.atchannel))
                || input.contains(context.getString(R.string.kurigohan))
                || input.contains(context.getString(R.string.kamehameha))) {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.SENPAI_DONT_TELL],
                    voiceLines[VoiceLine.Line.STILL_NOT_HAPPY]
            };
            speak(specificLines[randomgen.nextInt(specificLines.length)]);
        } else if (input.contains(context.getString(R.string.salieri))
                || input.contains(context.getString(R.string.maho))
                || input.contains(context.getString(R.string.hiyajo))) {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.SENPAI_QUESTION],
                    voiceLines[VoiceLine.Line.SENPAI_WHAT_WE_TALKING],
                    voiceLines[VoiceLine.Line.SENPAI_QUESTIONMARK],
                    voiceLines[VoiceLine.Line.SENPAI_WHO_IS_THIS]
            };
            speak(specificLines[randomgen.nextInt(specificLines.length)]);
        } else if (input.contains(context.getString(R.string.time_machine))
                || input.contains(context.getString(R.string.cern))
                || input.contains(context.getString(R.string.time_travel))) {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.TM_NONCENCE],
                    voiceLines[VoiceLine.Line.TM_YOU_SAID],
                    voiceLines[VoiceLine.Line.TM_NO_EVIDENCE],
                    voiceLines[VoiceLine.Line.TM_DONT_KNOW],
                    voiceLines[VoiceLine.Line.TM_NOT_POSSIBLE]
            };
            speak(specificLines[randomgen.nextInt(specificLines.length)]);
        } else if (input.contains(context.getString(R.string.memory))
                || input.contains(context.getString(R.string.amadeus))
                || input.contains(context.getString(R.string.science))) {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.HUMANS_SOFTWARE],
                    voiceLines[VoiceLine.Line.MEMORY_COMPLEXITY],
                    voiceLines[VoiceLine.Line.SECRET_DIARY],
                    voiceLines[VoiceLine.Line.MODIFIYING_MEMORIES],
                    voiceLines[VoiceLine.Line.MEMORIES_CHRISTINA]
            };
            speak(specificLines[randomgen.nextInt(specificLines.length)]);
        } else if (input.contains(context.getString(R.string.hello))
                || input.contains(context.getString(R.string.good_morning))
                || input.contains(context.getString(R.string.konnichiwa))
                || input.contains(context.getString(R.string.good_evening))) {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.HELLO],
                    voiceLines[VoiceLine.Line.NICE_TO_MEET_OKABE],
                    voiceLines[VoiceLine.Line.PLEASED_TO_MEET],
                    voiceLines[VoiceLine.Line.LOOKING_FORWARD_TO_WORKING]
            };
            speak(specificLines[randomgen.nextInt(specificLines.length)]);
        } else if (input.contains(context.getString(R.string.nice_body))
                || input.contains(context.getString(R.string.hot))
                || input.contains(context.getString(R.string.sexy))
                || input.contains(context.getString(R.string.boobies))
                || input.contains(context.getString(R.string.oppai))) {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.DEVILISH_PERVERT],
                    voiceLines[VoiceLine.Line.PERVERT_CONFIRMED],
                    voiceLines[VoiceLine.Line.PERVERT_IDIOT]
            };
            speak(specificLines[randomgen.nextInt(specificLines.length)]);
        } else if (input.contains(context.getString(R.string.robotics_notes))
                || input.contains(context.getString(R.string.antimatter))) {
            speak(voiceLines[VoiceLine.Line.HEHEHE]);
        } else {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.ASK_ME],
                    voiceLines[VoiceLine.Line.WHAT_DO_YOU_WANT],
                    voiceLines[VoiceLine.Line.WHAT_IS_IT],
                    voiceLines[VoiceLine.Line.HEHEHE],
                    voiceLines[VoiceLine.Line.WHY_SAY_THAT],
                    voiceLines[VoiceLine.Line.YOU_SURE]
            };
            speak(specificLines[randomgen.nextInt(specificLines.length)]);
        }
    }

    private class listener implements RecognitionListener {
        final String TAG = "Amadeus.listener";

        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "Speech recognition start");
        }
        public void onBeginningOfSpeech() {
            Log.d(TAG, "Listening speech");
        }
        public void onRmsChanged(float rmsdB) {
            //Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech() {
            Log.d(TAG, "Speech recognition end");
        }
        public void onError(int error) {
            Log.d(TAG,  "error " +  error);
            sr.cancel();
            speak(voiceLines[VoiceLine.Line.SORRY]);
        }
        public void onResults(Bundle results) {
            String input = "";
            String debug = "";
            Log.d(TAG, "Received results");
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for (Object word: data) {
                debug += word + "\n";
            }
            Log.d(TAG, debug);

            input += data.get(0);
            /* TODO: Japanese doesn't split the words. Sigh. */
            String[] splitInput = input.split(" ");

            /* Really, google? */
            if (splitInput[0].equalsIgnoreCase("Асистент")) {
                splitInput[0] = "Ассистент";
            }

            /* Switch language within current context for voice recognition */
            Context context = LangContext.load(getApplicationContext(), contextLang[0]);

            if (splitInput.length > 2 && splitInput[0].equalsIgnoreCase(context.getString(R.string.assistant))) {
                String cmd = splitInput[1].toLowerCase();
                String[] args = new String[splitInput.length - 2];
                System.arraycopy(splitInput, 2, args, 0, splitInput.length - 2);

                if (cmd.contains(context.getString(R.string.open))) {
                    openApp(args);
                }

            } else {
                answerSpeech(input, context);
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
