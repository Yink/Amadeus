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
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

class Amadeus {

    static Boolean isSpeaking = false;
    static Boolean isLoop = false;
    static MediaPlayer m;
    private static final String TAG = "Amadeus";
    private static int shaman_girls = -1;
    private static final VoiceLine[] voiceLines = VoiceLine.Line.getLines();
    private static final HashMap<Bundle<Integer>, Bundle<VoiceLine>> responseInputMap = new HashMap<>();

    static {
        responseInputMap.put(new Bundle<>(
                R.string.christina
        ), new Bundle<>(
                voiceLines[VoiceLine.Line.CHRISTINA],
                voiceLines[VoiceLine.Line.WHY_CHRISTINA],
                voiceLines[VoiceLine.Line.SHOULD_CHRISTINA],
                voiceLines[VoiceLine.Line.NO_TINA]
        ));
        responseInputMap.put(new Bundle<>(
                R.string.the_zombie, R.string.the_zombie2,
                R.string.celeb17
        ), new Bundle<>(
                voiceLines[VoiceLine.Line.DONT_CALL_ME_LIKE_THAT]
        ));
        responseInputMap.put(new Bundle<>(
                R.string.atchannel,
                R.string.kurigohan,
                R.string.kamehameha
        ), new Bundle<>(
                voiceLines[VoiceLine.Line.SENPAI_DONT_TELL],
                voiceLines[VoiceLine.Line.STILL_NOT_HAPPY]
        ));
        responseInputMap.put(new Bundle<>(
                R.string.salieri,
                R.string.maho,
                R.string.hiyajo
        ), new Bundle<>(
                voiceLines[VoiceLine.Line.SENPAI_QUESTION],
                voiceLines[VoiceLine.Line.SENPAI_WHAT_WE_TALKING],
                voiceLines[VoiceLine.Line.SENPAI_QUESTIONMARK],
                voiceLines[VoiceLine.Line.SENPAI_WHO_IS_THIS]
        ));
        responseInputMap.put(new Bundle<>(
                R.string.time_machine, R.string.time_travel2,
                R.string.cern,
                R.string.time_travel
        ), new Bundle<>(
                voiceLines[VoiceLine.Line.TM_NONCENCE],
                voiceLines[VoiceLine.Line.TM_YOU_SAID],
                voiceLines[VoiceLine.Line.TM_NO_EVIDENCE],
                voiceLines[VoiceLine.Line.TM_DONT_KNOW],
                voiceLines[VoiceLine.Line.TM_NOT_POSSIBLE]
        ));
        responseInputMap.put(new Bundle<>(
                R.string.memory,
                R.string.amadeus,
                R.string.science
        ), new Bundle<>(
                voiceLines[VoiceLine.Line.HUMANS_SOFTWARE],
                voiceLines[VoiceLine.Line.MEMORY_COMPLEXITY],
                voiceLines[VoiceLine.Line.SECRET_DIARY],
                voiceLines[VoiceLine.Line.MODIFIYING_MEMORIES],
                voiceLines[VoiceLine.Line.MEMORIES_CHRISTINA]
        ));
        responseInputMap.put(new Bundle<>(
                R.string.hello,
                R.string.good_morning,
                R.string.konnichiwa,
                R.string.good_evening
        ), new Bundle<>(
                voiceLines[VoiceLine.Line.HELLO],
                voiceLines[VoiceLine.Line.NICE_TO_MEET_OKABE],
                voiceLines[VoiceLine.Line.PLEASED_TO_MEET],
                voiceLines[VoiceLine.Line.LOOKING_FORWARD_TO_WORKING]
        ));
        responseInputMap.put(new Bundle<>(
                R.string.nice_body,
                R.string.hot,
                R.string.sexy,
                R.string.boobies,
                R.string.oppai
        ), new Bundle<>(
                voiceLines[VoiceLine.Line.DEVILISH_PERVERT],
                voiceLines[VoiceLine.Line.PERVERT_CONFIRMED],
                voiceLines[VoiceLine.Line.PERVERT_IDIOT]
        ));
        responseInputMap.put(new Bundle<>(
                R.string.robotics_notes,
                R.string.antimatter
        ), new Bundle<>(
                voiceLines[VoiceLine.Line.HEHEHE]
        ));
    }

    static void speak(VoiceLine line, final Activity activity) {
        final AnimationDrawable animation;
        final TextView subtitles = activity.findViewById(R.id.textView_subtitles);
        final ImageView kurisu = activity.findViewById(R.id.imageView_kurisu);
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

            m.setOnPreparedListener(mp -> {
                isSpeaking = true;
                mp.start();
                v.setEnabled(true);
            });

            m.setOnCompletionListener(mp -> {
                isSpeaking = false;
                mp.release();
                v.setEnabled(false);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kurisu.setImageDrawable(animation.getFrame(0));
                    }
                });
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

                            activity.runOnUiThread(() -> {
                                if (normalized > 50) {
                                    // Todo: Maybe choose sprite based on previous choice and volume instead of random
                                    kurisu.setImageDrawable(animation.getFrame((int) Math.ceil(Math.random() * 2)));
                                } else {
                                    kurisu.setImageDrawable(animation.getFrame(0));
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
        VoiceLine[] specificLines = null;
        input = input.toLowerCase();

        if (containInput(input,context.getString(R.string.nullpo))) {
            shaman_girls++;
            if(shaman_girls<5){
                specificLines = new VoiceLine[]{
                        voiceLines[VoiceLine.Line.GAH],
                        voiceLines[VoiceLine.Line.GAH_EXTENDED]
                };
            }else {
                VoiceLine singleLine;
                switch (shaman_girls) {
                    case 5:
                        singleLine = new VoiceLine(R.raw.leskinen_awesome, VoiceLine.Mood.WINKING, R.string.line_Leskinen_awesome);
                        break;
                    case 6:
                        singleLine = new VoiceLine(R.raw.leskinen_nice, VoiceLine.Mood.WINKING, R.string.line_Leskinen_nice);
                        break;
                    case 7:
                        singleLine = new VoiceLine(R.raw.leskinen_oh_no, VoiceLine.Mood.WINKING, R.string.line_Leskinen_oh_no);
                        break;
                    case 8:
                        singleLine = new VoiceLine(R.raw.leskinen_shaman, VoiceLine.Mood.WINKING, R.string.line_Leskinen_shaman);
                        break;
                    case 9:
                    default:
                        singleLine = new VoiceLine(R.raw.leskinen_holy_cow, VoiceLine.Mood.WINKING, R.string.line_Leskinen_holy_cow);
                        shaman_girls = 0;
                        break;
                }
                specificLines = new VoiceLine[]{singleLine};
            }
        } else {
            for (Bundle<Integer> input_bundle : responseInputMap.keySet()) {
                for (Integer input_code : input_bundle) {
                    if (containInput(input, context.getString(input_code))) {
                        specificLines = responseInputMap.get(input_bundle).toArray();
                        break;
                    }
                }
            }

            if (specificLines == null) {
                specificLines = new VoiceLine[]{
                        voiceLines[VoiceLine.Line.ASK_ME],
                        voiceLines[VoiceLine.Line.WHAT_DO_YOU_WANT],
                        voiceLines[VoiceLine.Line.WHAT_IS_IT],
                        voiceLines[VoiceLine.Line.HEHEHE],
                        voiceLines[VoiceLine.Line.WHY_SAY_THAT],
                        voiceLines[VoiceLine.Line.YOU_SURE]
                };
            }
        }
        int intTarget = 0;
        if(specificLines.length>1){intTarget = new Random().nextInt(specificLines.length);}
        Amadeus.speak(specificLines[intTarget], activity);
    }

    private static boolean containInput(final String input, final String... strings){
        for(String s:strings){
            if(input.contains(s)) return true;
        }
        return false;
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

    private static class Bundle<T> implements Iterable<T> {

        private final T[] list;

        @SafeVarargs
        private Bundle(T... list) {
            this.list = list;
        }

        private T[] toArray() {
            return list;
        }

        @NonNull
        @Override
        public Iterator<T> iterator() {
            Iterator<T> iterator;
            iterator = new Iterator<T>() {
                int index = 0;

                @Override
                public boolean hasNext() {
                    return index < list.length;
                }

                @Override
                public T next() {
                    return list[index++];
                }
            };
            return iterator;
        }
    }

}
