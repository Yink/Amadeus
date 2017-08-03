package com.example.yink.amadeus;

import android.app.Activity;
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
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

class Amadeus {

    private static String TAG = "Amadeus";

    private static int shaman_girls = -1;
    private static VoiceLine[] voiceLines = VoiceLine.Line.getLines();

    static Boolean isSpeaking = false;
    static Boolean isLoop = false;
    static MediaPlayer m;

    static void speak(VoiceLine line, final Activity activity) {
        final AnimationDrawable animation;
        final TextView subtitles = (TextView) activity.findViewById(R.id.textView_subtitles);
        final ImageView kurisu = (ImageView) activity.findViewById(R.id.imageView_kurisu);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);

        try {
            m = MediaPlayer.create(activity, line.getId());
            final Visualizer v = new Visualizer(m.getAudioSessionId());

            if (settings.getBoolean("show_subtitles", false)) {
                subtitles.setText(line.getSubtitle());
            }

            Resources res = activity.getResources();
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

                    activity.runOnUiThread(new Runnable() {
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

                            activity.runOnUiThread(new Runnable() {
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

    static void responseToInput(String input, Context context, Activity activity) {
        Random randomgen = new Random();

        input = input.toLowerCase();

        if (input.contains(context.getString(R.string.christina))) {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.CHRISTINA],
                    voiceLines[VoiceLine.Line.WHY_CHRISTINA],
                    voiceLines[VoiceLine.Line.SHOULD_CHRISTINA],
                    voiceLines[VoiceLine.Line.NO_TINA]
            };
            Amadeus.speak(specificLines[randomgen.nextInt(specificLines.length)], activity);
        } else if (input.contains(context.getString(R.string.nullpo))) {
            shaman_girls++;
            if (shaman_girls < 5) {
                VoiceLine[] specificLines = {
                        voiceLines[VoiceLine.Line.GAH],
                        voiceLines[VoiceLine.Line.GAH_EXTENDED]
                };
                Amadeus.speak(specificLines[randomgen.nextInt(specificLines.length)], activity);
            } else {
                switch (shaman_girls) {
                    case 5:
                        Amadeus.speak(new VoiceLine(R.raw.leskinen_awesome, VoiceLine.Mood.WINKING, R.string.line_Leskinen_awesome), activity);
                        break;
                    case 6:
                        Amadeus.speak(new VoiceLine(R.raw.leskinen_nice, VoiceLine.Mood.WINKING, R.string.line_Leskinen_nice), activity);
                        break;
                    case 7:
                        Amadeus.speak(new VoiceLine(R.raw.leskinen_oh_no, VoiceLine.Mood.WINKING, R.string.line_Leskinen_oh_no), activity);
                        break;
                    case 8:
                        Amadeus.speak(new VoiceLine(R.raw.leskinen_shaman, VoiceLine.Mood.WINKING, R.string.line_Leskinen_shaman), activity);
                        break;
                    case 9:
                        Amadeus.speak(new VoiceLine(R.raw.leskinen_holy_cow, VoiceLine.Mood.WINKING, R.string.line_Leskinen_holy_cow), activity);
                        shaman_girls = 0;
                        break;
                }
            }
        } else if (input.contains(context.getString(R.string.the_zombie))
                || input.contains(context.getString(R.string.celeb17))) {
            Amadeus.speak(voiceLines[VoiceLine.Line.DONT_CALL_ME_LIKE_THAT], activity);
        } else if (input.contains(context.getString(R.string.atchannel))
                || input.contains(context.getString(R.string.kurigohan))
                || input.contains(context.getString(R.string.kamehameha))) {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.SENPAI_DONT_TELL],
                    voiceLines[VoiceLine.Line.STILL_NOT_HAPPY]
            };
            Amadeus.speak(specificLines[randomgen.nextInt(specificLines.length)], activity);
        } else if (input.contains(context.getString(R.string.salieri))
                || input.contains(context.getString(R.string.maho))
                || input.contains(context.getString(R.string.hiyajo))) {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.SENPAI_QUESTION],
                    voiceLines[VoiceLine.Line.SENPAI_WHAT_WE_TALKING],
                    voiceLines[VoiceLine.Line.SENPAI_QUESTIONMARK],
                    voiceLines[VoiceLine.Line.SENPAI_WHO_IS_THIS]
            };
            Amadeus.speak(specificLines[randomgen.nextInt(specificLines.length)], activity);
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
            Amadeus.speak(specificLines[randomgen.nextInt(specificLines.length)], activity);
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
            Amadeus.speak(specificLines[randomgen.nextInt(specificLines.length)], activity);
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
            Amadeus.speak(specificLines[randomgen.nextInt(specificLines.length)], activity);
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
            Amadeus.speak(specificLines[randomgen.nextInt(specificLines.length)], activity);
        } else if (input.contains(context.getString(R.string.robotics_notes))
                || input.contains(context.getString(R.string.antimatter))) {
            Amadeus.speak(voiceLines[VoiceLine.Line.HEHEHE], activity);
        } else {
            VoiceLine[] specificLines = {
                    voiceLines[VoiceLine.Line.ASK_ME],
                    voiceLines[VoiceLine.Line.WHAT_DO_YOU_WANT],
                    voiceLines[VoiceLine.Line.WHAT_IS_IT],
                    voiceLines[VoiceLine.Line.HEHEHE],
                    voiceLines[VoiceLine.Line.WHY_SAY_THAT],
                    voiceLines[VoiceLine.Line.YOU_SURE]
            };
            Amadeus.speak(specificLines[randomgen.nextInt(specificLines.length)], activity);
        }
    }

    static void openApp(String[] input, Activity activity) {
        final PackageManager pm = activity.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        HashMap<String, Integer> dictionary = new HashMap<>();
        String corrected;
        Boolean found;
        /* TODO: Dictionary for other language equivalents. To be reworked. */
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
                Log.d(TAG, "Found app!");
                Intent app;
                Amadeus.speak(voiceLines[VoiceLine.Line.OK], activity);
                switch (packageInfo.packageName) {
                    /* Exceptional cases */
                    case "com.android.phone": {
                        app = new Intent(Intent.ACTION_DIAL, null);
                        activity.startActivity(app);
                        break;
                    }
                    case "com.android.chrome": {
                        app = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
                        /* Default browser might be different */
                        app.setPackage(packageInfo.packageName);
                        activity.startActivity(app);
                        break;
                    }
                    default: {
                        app = activity.getPackageManager().getLaunchIntentForPackage(packageInfo.packageName);
                        /* Check if intent is not null to avoid crash */
                        if (app != null) {
                            app.addCategory(Intent.CATEGORY_LAUNCHER);
                            activity.startActivity(app);
                        }
                        break;
                    }
                }
                /* Don't need to search for other ones, so break this loop */
                break;
            }
        }
    }

}
